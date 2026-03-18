package com.ai.chat.auth.util;

import com.ai.chat.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 *  JWT 工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;


    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>() ;
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "access");

        return generateToken(claims, jwtProperties.getAccessTokenExpire());
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "refresh");

        return generateToken(claims, jwtProperties.getRefreshTokenExpire());
    }

    /**
     * 生成令牌
     */
    private String generateToken(Map<String, Object> claims, Long expireSenconds) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireSenconds * 1000);

        return Jwts.builder()                           // 1. 创建 JWT 构建器
                .claims(claims)                             // 2. 添加声明（payload）
                .issuer(jwtProperties.getIssuer())          // 3. 设置签发者
                .issuedAt(now)                              // 4. 设置签发时间
                .expiration(expireDate)                     // 5. 设置过期时间
                .signWith(getSecretKey())                   // 6. 用密钥签名
                .compact();                                 // 7. 生成最终 JWT 字符串
    }

    /**
     * 解析令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析令牌失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }


    /**
     * 从令牌中获取用户
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 验证令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return true;
        }
        Date exipireDate = claims.getExpiration();
        return exipireDate.before(new Date());
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("验证令牌失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取令牌国企时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

}
