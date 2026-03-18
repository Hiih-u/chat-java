package com.ai.chat.auth.filter;


import com.ai.chat.auth.service.AuthService;
import com.ai.chat.auth.util.JwtUtil;
import com.ai.chat.auth.util.WebUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 *  JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取 Token
        String token = WebUtil.getTokenFromRequest(request);

        if(token != null) {
            try {
                // 验证 Token
                if (authService.validateToken(token)) {
                    // 从 Token 中获取用户ID
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);

                    if (userId != null && username != null) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        new ArrayList<>()
                                );
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // 设置到 Security 上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("JWT 认证成功，userId: {}, username: {}", userId, username);
                    }
                } else {
                    log.warn("JWT 令牌校验失败");
                }
            }catch (Exception e) {
                log.error("JWT 认证失败：{}", e.getMessage());
            }
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}
