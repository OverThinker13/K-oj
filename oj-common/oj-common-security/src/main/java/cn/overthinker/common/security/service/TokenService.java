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

    public String createToken(Long userId, String secret, Integer identity, String nickName, String headImage) {
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString();
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret);
        //第三方机制中存储敏感的信息

        //身份认证具体还要存储那些信息   redis 表明用户身份字段  identity  1  表示普通用户  2 ： 表示管理员用户  对象

        //使用什么样的数据结构  String  key value    String   hash  list  zset  set
        //key 必须保证唯一     便于维护  统一前缀：logintoken:userId   userId是通过雪花算法生成的
        //自增  管理员  C端用户   1
        //过期时间我们怎么记录  过期时间应该定多长。     720分钟   2~3小时

        String tokenKey = getTokenKey(userKey);
//            String tokenKey = "logintoken:" + sysUser.getUserId();
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity);
        loginUser.setNickName(nickName);
        loginUser.setHeadImage(headImage);
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
