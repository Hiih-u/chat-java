package com.ai.chat.auth.client;

import com.ai.chat.auth.pojo.dto.UserInfo;
import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.pojo.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * UserClient 降级处理
 */
@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public Result<UserInfo> getUserByUsername(String username) {
        log.error("调用 chat-system 获取用户信息失败，username: {}", username);
        return Result.error(ResultCode.SERVICE_ERROR.getCode(), "用户服务暂时不可用");
    }

    @Override
    public Result<UserInfo> getUserById(Long userId) {
        log.error("调用 chat-system 获取用户信息失败，userId: {}", userId);
        return Result.error(ResultCode.SERVICE_ERROR.getCode(), "用户服务暂时不可用");
    }
}
