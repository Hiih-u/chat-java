package com.ai.chat.service.impl;

import com.ai.chat.common.enums.ResultCode;
import com.ai.chat.common.exception.BusinessException;
import com.ai.chat.mapper.UserMapper;
import com.ai.chat.pojo.dto.UserCreateDTO;
import com.ai.chat.pojo.dto.UserUpdateDTO;
import com.ai.chat.pojo.entity.User;
import com.ai.chat.pojo.vo.UserVo;
import com.ai.chat.service.IUserService;
import com.ai.chat.wrapper.UserWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户管理 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements IUserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVo createUser(UserCreateDTO dto) {
        // 检查用户名唯一性
        boolean usernameExists = this.lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .exists();
        if (usernameExists) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        // 检查手机号唯一性
        if (StringUtils.hasText(dto.getPhone())) {
            boolean phoneExists = this.lambdaQuery()
                    .eq(User::getPhone, dto.getPhone())
                    .exists();
            if (phoneExists) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "手机号已被注册");
            }
        }
        // 检查邮箱唯一性
        if (StringUtils.hasText(dto.getEmail())) {
            boolean emailExists = this.lambdaQuery()
                    .eq(User::getEmail, dto.getEmail())
                    .exists();
            if (emailExists) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱已被注册");
            }
        }

        User user = UserWrapper.toEntity(dto);
        // 密码加密
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        boolean saved = this.save(user);
        if (!saved) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "用户创建失败");
        }
        log.info("用户创建成功: id={}, username={}", user.getId(), user.getUsername());
        return UserWrapper.toVo(user);
    }

    @Override
    public UserVo getUserById(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return UserWrapper.toVo(user);
    }

    @Override
    public UserVo getUserByUsername(String username) {
        User user = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return UserWrapper.toVo(user);
    }

    @Override
    public IPage<UserVo> pageUsers(int current, int size, String username, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        IPage<User> page = this.page(new Page<>(current, size), wrapper);
        return page.convert(UserWrapper::toVo);
    }

    @Override
    public List<UserVo> listAllUsers() {
        List<User> users = this.lambdaQuery()
                .orderByDesc(User::getCreatedAt)
                .list();
        return UserWrapper.toVoList(users);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVo updateUser(UserUpdateDTO dto) {
        User existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        // 用户名变更时检查唯一性
        if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(existing.getUsername())) {
            boolean usernameExists = this.lambdaQuery()
                    .eq(User::getUsername, dto.getUsername())
                    .exists();
            if (usernameExists) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
            }
            existing.setUsername(dto.getUsername());
        }
        // 手机号变更时检查唯一性
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(existing.getPhone())) {
            boolean phoneExists = this.lambdaQuery()
                    .eq(User::getPhone, dto.getPhone())
                    .ne(User::getId, dto.getId())
                    .exists();
            if (phoneExists) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "手机号已被注册");
            }
            existing.setPhone(dto.getPhone());
        }
        // 邮箱变更时检查唯一性
        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(existing.getEmail())) {
            boolean emailExists = this.lambdaQuery()
                    .eq(User::getEmail, dto.getEmail())
                    .ne(User::getId, dto.getId())
                    .exists();
            if (emailExists) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱已被注册");
            }
            existing.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getRealName())) {
            existing.setRealName(dto.getRealName());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getTenantId() != null) {
            existing.setTenantId(dto.getTenantId());
        }

        boolean updated = this.updateById(existing);
        if (!updated) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "用户更新失败");
        }
        log.info("用户更新成功: id={}", existing.getId());
        return UserWrapper.toVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        boolean removed = this.removeById(id);
        if (!removed) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "用户删除失败");
        }
        log.info("用户删除成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "删除的用户ID列表不能为空");
        }
        boolean removed = this.removeByIds(ids);
        if (!removed) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "批量删除用户失败");
        }
        log.info("批量删除用户成功: ids={}", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        log.info("用户密码修改成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        log.info("用户密码重置成功: id={}", id);
    }
}
