package com.ai.chat.system.feign;

import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.system.entity.UserInfo;
import com.ai.chat.system.fallback.IUserClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * chat-system 用户服务 Feign 客户端接口（契约）
 *
 * <p>基于 Blade 封装模式：
 * <ul>
 *   <li>此接口定义在 chat-system-api 模块，作为服务间调用契约</li>
 *   <li>chat-system 中的 UserInternalController 实现此接口，保证路径一致</li>
 *   <li>消费方（如 chat-auth）引入 chat-system-api 依赖后直接注入此接口使用</li>
 * </ul>
 */
@FeignClient(
        value = "chat-system",
        fallbackFactory = IUserClientFallbackFactory.class
)
public interface IUserClient {

    /**
     * 内部接口路由前缀
     */
    String API_PREFIX = "/api/user/info";

    /**
     * 根据用户名获取用户信息（含密码，仅供内部认证使用）
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping(API_PREFIX + "/username/{username}")
    Result<UserInfo> getUserByUsername(@PathVariable("username") String username);

    /**
     * 根据用户ID获取用户信息（含密码，仅供内部认证使用）
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping(API_PREFIX + "/{userId}")
    Result<UserInfo> getUserById(@PathVariable("userId") Long userId);
}
