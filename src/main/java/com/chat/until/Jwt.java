package com.chat.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/*
* 建立失效时间30分钟
* 密钥
*
* */
@Component
public class Jwt {

    /* 签名密钥，HS256 要求至少 32 字节 */
    private final SecretKey key;

    /* token 有效期（毫秒） */
    private final long expireMillis;

    public Jwt(@Value("${jwt.secret}") String secret,
               @Value("${jwt.expire-minutes:30}") long expireMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60 * 1000L;
    }

    /* 签发 token：把 id、role 这类自定义信息放进 claims */
    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(key)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .compact();
    }

    /*
     * 解析 token，顺便校验签名和有效期。
     * 过期或被篡改会抛异常，由调用方（拦截器）统一处理。
     */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
