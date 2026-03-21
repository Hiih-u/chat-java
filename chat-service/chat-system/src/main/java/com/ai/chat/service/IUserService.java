package com.ai.chat.service;

import com.ai.chat.pojo.dto.UserCreateDTO;
import com.ai.chat.pojo.dto.UserUpdateDTO;
import com.ai.chat.pojo.entity.User;
import com.ai.chat.pojo.vo.UserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户管理 Service 接口
 */
public interface IUserService extends IService<User> {

    /**
     * 创建用户
     *
     * @param dto 创建请求
     * @return 用户视图
     */
    UserVo createUser(UserCreateDTO dto);

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户视图
     */
    UserVo getUserById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户视图
     */
    UserVo getUserByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param current  当前页
     * @param size     每页条数
     * @param username 用户名（模糊，可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    IPage<UserVo> pageUsers(int current, int size, String username, Integer status);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<UserVo> listAllUsers();

    /**
     * 更新用户信息
     *
     * @param dto 更新请求
     * @return 更新后的用户视图
     */
    UserVo updateUser(UserUpdateDTO dto);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户（逻辑删除）
     *
     * @param ids 用户ID列表
     */
    void deleteUsers(List<Long> ids);

    /**
     * 修改用户密码
     *
     * @param id          用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置用户密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
}
