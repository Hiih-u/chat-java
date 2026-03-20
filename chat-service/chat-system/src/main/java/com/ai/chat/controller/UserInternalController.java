package com.ai.chat.controller;

import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.mapper.SysUserMapper;
import com.ai.chat.pojo.entity.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户内部接口（供 chat-auth 调用）
 */
@Slf4j
@RestController
@RequestMapping("/api/user/internal")
@RequiredArgsConstructor
public class UserInternalController {

    private final SysUserMapper sysUserMapper;

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public Result<SysUser> getUserByUsername(@PathVariable String username) {
        log.info("内部接口：根据用户名获取用户信息，username: {}", username);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        return Result.success(user);
    }

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<SysUser> getUserById(@PathVariable Long userId) {
        log.info("内部接口：根据用户ID获取用户信息，userId: {}", userId);

        SysUser user = sysUserMapper.selectById(userId);

        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        return Result.success(user);
    }
}
