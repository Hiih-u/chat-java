package com.ai.chat.common.mapper;

import com.ai.chat.common.entity.GeminiServiceNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GeminiServiceNodeMapper extends BaseMapper<GeminiServiceNode> {
}
