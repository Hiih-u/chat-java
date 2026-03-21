package com.ai.chat.controller;

import com.ai.chat.common.pojo.entity.Result;
import com.ai.chat.system.pojo.dto.UserCreateDTO;
import com.ai.chat.system.pojo.dto.UserUpdateDTO;
import com.ai.chat.system.pojo.vo.UserVo;
import com.ai.chat.service.IUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 提供用户的 CRUD 操作接口
 */
@Tag(name = "用户管理", description = "用户管理接口")
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final IUserService userService;

    /**
     * 创建用户
     *
     * @param dto 用户创建 DTO
     * @return 创建的用户信息
     */
    @Operation(summary = "创建用户", description = "创建一个新用户")
    @PostMapping("/create")
    public Result<UserVo> create(@Valid @RequestBody UserCreateDTO dto) {
        log.info("创建用户: username={}", dto.getUsername());
        UserVo vo = userService.createUser(dto);
        log.info("用户创建成功: id={}", vo.getId());
        return Result.success(vo);
    }

    /**
     * 根据 ID 查询用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID查询用户详情")
    @GetMapping("/{id}")
    public Result<UserVo> getById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        log.info("查询用户: id={}", id);
        return Result.success(userService.getUserById(id));
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Operation(summary = "根据用户名查询", description = "根据用户名精确查询用户信息")
    @GetMapping("/username/{username}")
    public Result<UserVo> getByUsername(
            @Parameter(description = "用户名", required = true)
            @PathVariable String username) {
        log.info("根据用户名查询: username={}", username);
        return Result.success(userService.getUserByUsername(username));
    }

    /**
     * 分页查询用户列表
     *
     * @param current  当前页码，默认1
     * @param size     每页条数，默认10
     * @param username 用户名（模糊查询，可选）
     * @param status   状态过滤（可选）
     * @return 分页用户列表
     */
    @Operation(summary = "分页查询用户", description = "分页查询用户列表，支持按用户名模糊搜索和状态过滤")
    @GetMapping("/page")
    public Result<IPage<UserVo>> page(
            @Parameter(description = "当前页码")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int current,
            @Parameter(description = "每页条数")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页条数最小为1") int size,
            @Parameter(description = "用户名（模糊查询）")
            @RequestParam(required = false) String username,
            @Parameter(description = "状态：1-正常 0-禁用")
            @RequestParam(required = false) Integer status) {
        log.info("分页查询用户: current={}, size={}, username={}, status={}", current, size, username, status);
        return Result.success(userService.pageUsers(current, size, username, status));
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Operation(summary = "查询所有用户", description = "查询系统中所有用户")
    @GetMapping("/list")
    public Result<List<UserVo>> list() {
        log.info("查询所有用户");
        return Result.success(userService.listAllUsers());
    }

    /**
     * 更新用户信息
     *
     * @param dto 用户更新 DTO
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户", description = "更新用户基本信息")
    @PutMapping("/update")
    public Result<UserVo> update(@Valid @RequestBody UserUpdateDTO dto) {
        log.info("更新用户: id={}", dto.getId());
        UserVo vo = userService.updateUser(dto);
        log.info("用户更新成功: id={}", vo.getId());
        return Result.success(vo);
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户", description = "根据ID删除用户（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除用户", description = "批量删除用户（逻辑删除）")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(
            @Parameter(description = "用户ID列表", required = true)
            @RequestBody @NotEmpty(message = "删除的用户ID列表不能为空") List<Long> ids) {
        log.info("批量删除用户: ids={}", ids);
        userService.deleteUsers(ids);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @PutMapping("/{id}/password")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "旧密码", required = true) @RequestParam String oldPassword,
            @Parameter(description = "新密码", required = true) @RequestParam String newPassword) {
        log.info("修改用户密码: id={}", id);
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 重置密码（管理员操作）
     *
     * @param id          用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "新密码", required = true) @RequestParam String newPassword) {
        log.info("重置用户密码: id={}", id);
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
}
