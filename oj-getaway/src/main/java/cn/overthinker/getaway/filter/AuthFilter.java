package cn.overthinker.getaway.filter;

import cn.hutool.core.util.StrUtil;
import cn.overthinker.getaway.properties.IgnoreWhiteProperties;
import com.alibaba.fastjson2.JSON;
import cn.overthinker.common.core.utils.JwtUtils;
import cn.overthinker.common.core.constants.CacheConstants;
import cn.overthinker.common.core.constants.HttpConstants;
import cn.overthinker.common.core.domain.LoginUser;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.core.enums.UserIdentity;
import cn.overthinker.common.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    // 排除过滤的uri白名单地址，在nacos自行添加
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    public static void main(String[] args) {
        AuthFilter authFilter = new AuthFilter();
        String pattern = "/sys/bc";
        System.out.println(authFilter.isMatch(pattern, "/sys/bc"));

        // 测试 ?
//        String pattern = "/sys/?bc";
//        System.out.println(authFilter.isMatch(pattern, "/sys/abc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/cbc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/acbc"));
//        System.out.println(authFilter.isMatch(pattern, "/sdsa/abc"));
//        System.out.println(authFilter.isMatch(pattern, "/sys/abcw"));

        // 测试*
        // String pattern = "/sys/*/bc";
        // System.out.println(authFilter.isMatch(pattern,"/sys/a/bc"));
        // System.out.println(authFilter.isMatch(pattern,"/sys/sdasdsadsad/bc"));
        // System.out.println(authFilter.isMatch(pattern,"/sys/a/b/bc"));
        // System.out.println(authFilter.isMatch(pattern,"/a/b/bc"));
        // System.out.println(authFilter.isMatch(pattern,"/sys/a/b/"));

        // 测试**
        // String pattern = "/sys/**/bc";
        // System.out.println(authFilter.isMatch(pattern, "/sys/a/bc"));
        // System.out.println(authFilter.isMatch(pattern, "/sys/sdasdsadsad/bc"));
        // System.out.println(authFilter.isMatch(pattern, "/sys/a/b/bc"));
        // System.out.println(authFilter.isMatch(pattern, "/sys/a/b/s/23/432/fdsf///bc"));
        // System.out.println(authFilter.isMatch(pattern, "/a/b/s/23/432/fdsf///bc"));
        // System.out.println(authFilter.isMatch(pattern, "/sys/a/b/s/23/432/fdsf///"));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();  // 请求接口地址

        // 跳过不需要验证的路径，通过接口白名单的所有接口均不需要进行身份认证
        if (matches(url, ignoreWhite.getWhites())) {   //判断如果当前的接口在白名单中不需要进行身份认证 ignoreWhite.getWhites():拿到Nacos上配置的接口地址的白名单
            return chain.filter(exchange);
        }

        // 执行到这说明接口不在白名单中，接着进行身份认证  通过token进行身份认证 首先要获取token
        // 从http请求头中获取token
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }

        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);  //获取令牌中的信息，解析payload中的信息，存储着用户唯一标识信息
            if (claims == null) {
                // springcooud getaway基于webflux
                return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
            }
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }

        //通过redis中存储的数据来控制jwt的过期时间
        String userKey = JwtUtils.getUserKey(claims);
        boolean isLogin = redisService.hasKey(getTokenKey(userKey));
        if (!isLogin) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }

        String userId = JwtUtils.getUserId(claims);
        if (StrUtil.isEmpty(userId)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        // 走到这说明token是正确的且没有过期，接下来判断redis存储的关于身份认证的信息是否是对的
        // 判断当前请求，如果请求时C端功能（只有C端用户可以请求）还是B端功能（只有管理员可以请求）
        LoginUser user = redisService.getCacheObject(getTokenKey(userKey), LoginUser.class);

        if (url.contains(HttpConstants.SYSTEM_URL_PREFIX) && !UserIdentity.ADMIN.getValue().equals(user.getIdentity())) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        if (url.contains(HttpConstants.FRIEND_URL_PREFIX) && !UserIdentity.ORDINARY.getValue().equals(user.getIdentity())) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        return chain.filter(exchange);
    }

    /**
     * 查找指定url是否匹配指定匹配规则链表中的任意一个字符串
     */
    private boolean matches(String url, List<String> patternList) {
        if (StrUtil.isEmpty(url) || CollectionUtils.isEmpty(patternList)) {
            return false;
        }
        // 判断接口地址如果和白名单一个地址匹配就返回true，如果遍历完白名单所有的地址都没有匹配就返回false
        for (String pattern : patternList) {
            if (isMatch(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则匹配
     * 匹配规则中：
     * pattern中可以写一些特殊字符
     * ? 表示单个字符
     * * 表示一层路径内的任意字符串，不可跨层级
     * ** 表示任意层路径
     */
    private boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * 获取缓存key
     */
    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }

    /**
     * 从请求头中获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpConstants.AUTHENTICATION);
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return webFluxResponseWriter(exchange.getResponse(), msg, ResultCode.FAILED_UNAUTHORIZED.getCode());
    }

    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String msg, int code) {
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        R<?> result = R.fail(code, msg);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return -200; // 值越小过滤器越先被执行
    }

}
