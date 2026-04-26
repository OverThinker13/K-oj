package cn.overthinker.common.core.utils;

import cn.overthinker.common.core.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class JwtUtils {

    /**
     * 生成令牌
     *
     * @param claims 数据
     * @param secret 密钥
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims, String secret) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从令牌中获取数据
     *
     * @param token  令牌
     * @param secret 密钥
     * @return 数据
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUserKey(Claims claims) {
        return toStr(claims.get(JwtConstants.LOGIN_USER_KEY));
    }

    public static String getUserId(Claims claims) {
        return toStr(claims.get(JwtConstants.LOGIN_USER_ID));
    }

    private static String toStr(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static void main(String[] args) {
//        Map<String, Object> claims = new HashMap<String, Object>();
//        claims.put("userId", 123456L);
//        //secret 必须保密 随机性 不能硬编码(配置在Nacos上) 定期更换()
//        System.out.println(createToken(claims, "zxcvbnmasdfghjklzxcvbnm"));
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEyMzQ1Nn0.n_FIDHn9kGgX6eQZiF0aqSEJhAOTm-1iwC0fj0_yT-8zHuD0m5lTpWYkGyr_j78yI8blUrmcTu512mU51FOBAw";
        System.out.println(parseToken(token, "zxcvbnmasdfghjklzxcvbnm"));
    }


    // 1.用户登录成功后，调用createToken方法生成令牌，并发送给客户端
    // 2.后续的所有请求在调用具体的接口之前都要先通过token进行身份认证
    // 3.用户使用系统的过程中我们需要进行合适的适时的延长jwt过期时间

}