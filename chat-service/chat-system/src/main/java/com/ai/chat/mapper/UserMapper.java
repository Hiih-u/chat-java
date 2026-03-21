package com.ai.chat.mapper;

import com.ai.chat.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
