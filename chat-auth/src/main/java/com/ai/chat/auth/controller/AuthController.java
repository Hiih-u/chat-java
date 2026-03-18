package com.ai.chat.auth.controller;

import com.ai.chat.auth.pojo.dto.LoginRequest;
import com.ai.chat.auth.pojo.vo.LoginResponse;
import com.ai.chat.auth.service.AuthService;
import com.ai.chat.auth.util.WebUtil;
import com.ai.chat.common.pojo.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                       HttpServletRequest httpRequest) {
        log.info("用户登录请求，username: {}, ip: {}",
                request.getUsername(), WebUtil.getClientIp(httpRequest));

        LoginResponse response = authService.login(request, httpRequest);
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest httpRequest) {
        String token = WebUtil.getTokenFromRequest(httpRequest);
        if (token != null) {
            authService.logout(token);
            log.info("用户登出成功");
        }
        return Result.success();
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        log.info("刷新令牌请求");
        LoginResponse response = authService.refreshToken(refreshToken);
        return Result.success(response);
    }

    /**
     * 验证令牌
     */
    @GetMapping("/validate")
    public Result<Boolean> validateToken(HttpServletRequest request) {
        String token = WebUtil.getTokenFromRequest(request);
        if (token == null) {
            return Result.error("令牌不能为空");
        }
        boolean valid = authService.validateToken(token);
        return Result.success(valid);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("chat-auth service is running");
    }
}
