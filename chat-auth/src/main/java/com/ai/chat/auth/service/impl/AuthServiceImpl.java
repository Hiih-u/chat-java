package com.ai.chat.auth.service.impl;

import com.ai.chat.auth.config.JwtProperties;
import com.ai.chat.auth.pojo.dto.LoginRequest;
import com.ai.chat.system.feign.IUserClient;
import com.ai.chat.auth.pojo.entity.UserToken;
import com.ai.chat.auth.pojo.vo.LoginResponse;
import com.ai.chat.auth.service.AuthService;
import com.ai.chat.auth.service.TokenService;
import com.ai.chat.auth.util.JwtUtil;
import com.ai.chat.auth.util.WebUtil;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.system.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final IUserClient userClient;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Result<User> userResult = userClient.getUserByUsername(request.getUsername());
        if (userResult.getCode() != 200 || userResult.getData() == null) {
            throw new BusinessException("用户名或密码错误");
        }
        User userInfo = userResult.getData();

        // 验证用户状态
        if (userInfo.getStatus() == null || userInfo.getStatus() != 1) {
            throw new BusinessException("用户被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), userInfo.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成 JWT 令牌
        String accessToken = jwtUtil.generateAccessToken(userInfo.getId(), userInfo.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userInfo.getId(), userInfo.getUsername());

        // 计算过期时间
        LocalDateTime accessExpireTime = LocalDateTime.now().plusSeconds(jwtProperties.getAccessTokenExpire());
        LocalDateTime refreshExpireTime = LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpire());

        UserToken userToken = new UserToken();
        userToken.setUserId(userInfo.getId());
        userToken.setUsername(userInfo.getUsername());
        userToken.setAccessToken(accessToken);
        userToken.setRefreshToken(refreshToken);
        userToken.setAccessExpireTime(accessExpireTime);
        userToken.setRefreshExpireTime(refreshExpireTime);
        userToken.setLoginIp(WebUtil.getClientIp(httpRequest));
        userToken.setUserAgent(WebUtil.getUserAgent(httpRequest));
        userToken.setDeviceType(request.getDeviceType());
        userToken.setCreatedAt(LocalDateTime.now());
        userToken.setUpdatedAt(LocalDateTime.now());

        try {
            tokenService.saveToken(userToken);
        } catch (Exception e) {
            log.error("保存令牌失败", e);
            throw new BusinessException("登录失败");
        }

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpire())
                .userId(userInfo.getId())
                .username(userInfo.getUsername())
                .tokenType("Bearer")
                .build();

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException("Token 不能为空");
        }
        tokenService.deleteToken(token);
        log.info("用户登出成功，token: {}", token.substring(0, Math.min(20, token.length())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }

        // 从令牌中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        if (userId == null || username == null) {
            throw new BusinessException("刷新令牌无效");
        }

        // 验证用户是否存在且状态正常
        Result<User> userResult = userClient.getUserById(userId);
        if (userResult.getCode() != 200 || userResult.getData() == null) {
            throw new BusinessException("用户不存在");
        }

        User userInfo = userResult.getData();
        if (userInfo.getStatus() == null || userInfo.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成新的访问令牌
        String newAccessToken = jwtUtil.generateAccessToken(userId, username);
        LocalDateTime accessExpireTime = LocalDateTime.now().plusSeconds(jwtProperties.getAccessTokenExpire());

        // 缓存新令牌
        tokenService.cacheToken(newAccessToken, userId, accessExpireTime);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpire())
                .userId(userId)
                .username(username)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 验证 JWT 格式和签名
        if (!jwtUtil.validateToken(token)) {
            return false;
        }

        // 验证令牌是否在缓存中（未被登出）
        return tokenService.isTokenInCache(token);
    }
}
