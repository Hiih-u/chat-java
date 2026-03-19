package com.ai.chat.auth.client;

import com.ai.chat.auth.pojo.dto.UserInfo;
import com.ai.chat.common.pojo.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Chat-System 用户服务 Feign 客户端
 */
@FeignClient(
        name = "chat-system",
        fallback = UserClientFallback.class
)
public interface UserClient {

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/api/user/internal/username/{username}")
    Result<UserInfo> getUserByUsername(@PathVariable("username") String username);

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/api/user/internal/{userId}")
    Result<UserInfo> getUserById(@PathVariable("userId") Long userId);
}
