package com.ai.chat.filter;

import com.ai.chat.config.AuthProperties;
import com.ai.chat.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 白名单放行
        if (isWhiteList(path)) {
            log.debug("白名单放行: {}", path);
            return chain.filter(exchange);
        }

        // 获取 Authorization Header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求缺少 Token，路径: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 解析 JWT，提取 userId
        try {
            String token = authHeader.substring(7);
            Key key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            log.debug("JWT 校验通过，userId: {}, 路径: {}", userId, path);

            // 将 userId 写入 Header 透传给下游服务
            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.header("X-User-Id", userId))
                    .build();
            return chain.filter(mutated);

        } catch (Exception e) {
            log.warn("JWT 校验失败，路径: {}, 原因: {}", path, e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }


    }

    private boolean isWhiteList(String path) {
        if (authProperties.getWhiteList() == null) {
            return false;
        }

        return authProperties.getWhiteList().stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        // 最高优先级，在所有路由过滤器之前执行
        return -100;
    }
}
