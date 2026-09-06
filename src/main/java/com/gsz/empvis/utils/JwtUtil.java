package com.gsz.empvis.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }

    /**
     * 生成 JWT
     */
    public String generateToken(Long userId, String username) {

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 JWT
     */
    public Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取用户名
     */
    public String getUsername(String token) {

        return parseToken(token).getSubject();
    }

    /**
     * 获取用户ID
     */
    public Long getUserId(String token) {

        Object userId = parseToken(token).get("userId");

        return Long.valueOf(userId.toString());
    }
}