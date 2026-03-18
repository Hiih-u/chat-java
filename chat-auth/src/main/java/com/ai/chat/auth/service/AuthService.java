package com.ai.chat.auth.service;

import com.ai.chat.auth.pojo.dto.LoginRequest;
import com.ai.chat.auth.pojo.vo.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 验证令牌
     */
    boolean validateToken(String token);
}
