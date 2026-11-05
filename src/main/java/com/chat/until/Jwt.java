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

/**
 * JWT 工具。
 * <p>
 * 和以前不一样的地方：
 * 1. 以前是 {@code Keys.secretKeyFor(...)} 每次启动随机生成一个密钥，
 * 服务一重启，所有人手里的 token 全部失效（表现为"莫名其妙被踢下线"）。
 * 现在密钥来自配置 {@code jwt.secret}，重启前后一致。
 * 2. 以前过期时间是 {@code 30 * 10000} 毫秒 = 5 分钟，太短。现在默认 30 分钟，可配。
 * <p>
 * 密钥必须是 32 字节以上（HS256 的要求），太短启动时会直接报错，这是故意的。
 */
@Component
public class Jwt {

    /** 签名密钥，HS256 要求至少 32 字节 */
    private final SecretKey key;

    /** token 有效期（毫秒） */
    private final long expireMillis;

    public Jwt(@Value("${jwt.secret}") String secret,
               @Value("${jwt.expire-minutes:30}") long expireMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60 * 1000L;
    }

    /** 签发 token：把 id、role 这类自定义信息放进 claims */
    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(key)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .compact();
    }

    /**
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
