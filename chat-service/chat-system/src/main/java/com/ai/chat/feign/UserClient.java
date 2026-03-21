package com.ai.chat.feign;

import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.pojo.entity.User;
import com.ai.chat.service.IUserService;
import com.ai.chat.system.api.feign.IUserClient;
import com.ai.chat.system.api.entity.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户内部服务接口控制器
 *
 * <p>实现 {@link IUserClient} 接口，保证路径与 Feign 契约完全一致（编译器校验）。
 * 此接口仅供内部服务（如 chat-auth）通过 Feign 调用，返回含密码的完整用户信息。
 * 生产环境应配合网关鉴权，禁止外部直接访问 /api/user/info/** 路径。
 */
@Tag(name = "用户内部接口", description = "仅供内部 Feign 调用，包含密码字段")
@Slf4j
@RestController
@RequestMapping(IUserClient.API_PREFIX)
@RequiredArgsConstructor
public class UserClient implements IUserClient {

    private final IUserService userService;

    /**
     * 根据用户名获取用户完整信息（含密码）
     */
    @Operation(summary = "[内部] 根据用户名查询用户", description = "含密码字段，仅供 chat-auth 调用")
    @Override
    @GetMapping("/username/{username}")
    public Result<UserInfo> getUserByUsername(
            @Parameter(description = "用户名", required = true)
            @PathVariable("username") String username) {
        log.info("[内部接口] 根据用户名查询用户: {}", username);
        User user = userService.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .eq(User::getDeleted, 0)
        );
        if (user == null) {
            return Result.error("用户不存在: " + username);
        }
        return Result.success(toUserInfo(user));
    }

    /**
     * 根据用户ID获取用户完整信息（含密码）
     */
    @Operation(summary = "[内部] 根据用户ID查询用户", description = "含密码字段，仅供 chat-auth 调用")
    @Override
    @GetMapping("/{userId}")
    public Result<UserInfo> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("userId") Long userId) {
        log.info("[内部接口] 根据用户ID查询用户: {}", userId);
        User user = userService.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            return Result.error("用户不存在: " + userId);
        }
        return Result.success(toUserInfo(user));
    }

    /**
     * 将 User 实体转换为 UserInfo（含密码，内部专用）
     */
    private UserInfo toUserInfo(User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setPassword(user.getPassword());
        info.setPhone(user.getPhone());
        info.setEmail(user.getEmail());
        info.setRealName(user.getRealName());
        info.setStatus(user.getStatus());
        info.setTenantId(user.getTenantId());
        return info;
    }
}
