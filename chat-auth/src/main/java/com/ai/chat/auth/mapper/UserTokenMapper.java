package com.ai.chat.auth.mapper;

import com.ai.chat.auth.pojo.entity.UserToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {
}
