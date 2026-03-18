package com.ai.chat.auth.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret;

    /**
     * 访问令牌过期时间（秒）
     */
    private Long accessTokenExpire;

    /**
     * 刷新令牌过期时间（秒）
     */
    private Long refreshTokenExpire;

    /**
     * 签发者
     */
    private String issuer;
}
