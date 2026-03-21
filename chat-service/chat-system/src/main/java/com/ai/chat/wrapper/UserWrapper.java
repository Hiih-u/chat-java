package com.ai.chat.wrapper;

import com.ai.chat.system.pojo.dto.UserCreateDTO;
import com.ai.chat.system.pojo.entity.User;
import com.ai.chat.system.pojo.vo.UserVo;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 DTO/VO 转换工具类
 */
public class UserWrapper {

    private UserWrapper() {
    }

    /**
     * CreateDTO 转 Entity
     */
    public static User toEntity(UserCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    /**
     * Entity 转 VO（脱敏：不返回密码）
     */
    public static UserVo toVo(User user) {
        if (user == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * Entity 列表转 VO 列表
     */
    public static List<UserVo> toVoList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserWrapper::toVo)
                .collect(Collectors.toList());
    }
}
