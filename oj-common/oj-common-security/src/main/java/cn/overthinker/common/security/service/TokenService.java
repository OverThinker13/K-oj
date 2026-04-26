package cn.overthinker.common.security.service;

import cn.hutool.core.lang.UUID;
import cn.overthinker.common.core.constants.CacheConstants;
import cn.overthinker.common.core.constants.JwtConstants;
import cn.overthinker.common.redis.service.RedisService;
import cn.overthinker.common.core.domain.LoginUser;
import cn.overthinker.common.core.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 提供用户登录token的方法
@Service
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId, String secret, Integer identity) {
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

        String key = CacheConstants.LOGIN_TOKEN_KEY + userKey;
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity);
        redisService.setCacheObject(key, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);

        return token;
    }
}
