package cn.overthinker.common.security.service;

import cn.hutool.core.lang.UUID;
import cn.overthinker.common.core.constants.CacheConstants;
import cn.overthinker.common.core.constants.JwtConstants;
import cn.overthinker.common.redis.service.RedisService;
import cn.overthinker.common.core.domain.LoginUser;
import cn.overthinker.common.core.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 提供用户登录token的方法
@Slf4j
@Service
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId, String secret, Integer identity, String nickName) {
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString();
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret);
        // 身份认证具体需要存储那些敏感信息

        // 第三方机制中存储敏感信息 redis 表面用户身份的字段 identity 1表示普通用户 2表示管理员用户 用对象存储

        // 使用什么样的数据结构存储  key value  String
        // key必须保证唯一且便于维护 - 统一前缀：logintoken:userId （userId通过雪花算法生成的唯一主键）
        // 或者也可以用Hutool工具包的一个方法fastUUID

        // 过期时间怎么记录  过期时间应该定多长，由于还没有上线项目没有数据，我们先在这里设置相对比较安全的过期时间

        String tokenKey = getTokenKey(userKey);  //使用的是userKey
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity);
        loginUser.setNickName(nickName);
        redisService.setCacheObject(tokenKey, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);

        return token;
    }

    // 延长token的有效时间，就是延长redis当中存储的用于身份认证民房信息的有效时间，注意不是jwt过期时间
    // 在身份认证通过之后，并且在请求到达controller层之前才会调用这个方法
    public void extendToken(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return;
        }
        String tokenKey = getTokenKey(userKey);

        //720min 剩余180min时候对它进行延长
        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if (expire != null && expire < CacheConstants.REFRESH_TIME) {
            redisService.expire(tokenKey, CacheConstants.EXP, TimeUnit.MINUTES);
        }

    }

    private String getTokenKey(String userKey) {
        return CacheConstants.LOGIN_TOKEN_KEY + userKey;
    }


    public LoginUser getLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return null;
        }
        return redisService.getCacheObject(getTokenKey(userKey), LoginUser.class);
    }

    public boolean deleteLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return false;
        }
        return redisService.deleteObject(getTokenKey(userKey));
    }

    private String getUserKey(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);
            if (claims == null) {
                log.error("解析token:{} 出现异常：", token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析token:{} 出现异常：", token, e);
            return null;
        }
        return JwtUtils.getUserKey(claims);  //获取jwt里面的key
    }


}
