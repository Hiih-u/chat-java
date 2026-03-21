package com.ai.chat.system.fallback;

import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.system.api.IUserClient;
import com.ai.chat.system.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * IUserClient 降级工厂（Blade 模式）
 *
 * <p>使用 {@link FallbackFactory} 替代简单 Fallback，可以捕获具体异常信息，便于排查问题。
 * 消费方（如 chat-auth）引入 chat-system-api 后，此 @Component 会被扫描并注册。
 */
@Slf4j
@Component
public class IUserClientFallbackFactory implements FallbackFactory<IUserClient> {

    @Override
    public IUserClient create(Throwable cause) {
        return new IUserClient() {
            @Override
            public Result<UserInfo> getUserByUsername(String username) {
                log.error("[IUserClient] getUserByUsername 调用失败, username={}, cause: {}",
                        username, cause.getMessage());
                return Result.error(ResultCode.SERVICE_ERROR.getCode(), "用户服务暂不可用");
            }

            @Override
            public Result<UserInfo> getUserById(Long userId) {
                log.error("[IUserClient] getUserById 调用失败, userId={}, cause: {}",
                        userId, cause.getMessage());
                return Result.error(ResultCode.SERVICE_ERROR.getCode(), "用户服务暂不可用");
            }
        };
    }
}
