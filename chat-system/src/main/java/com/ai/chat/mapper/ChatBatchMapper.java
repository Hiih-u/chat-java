package com.ai.chat.mapper;

import com.ai.chat.common.pojo.entity.ChatBatch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChatBatchMapper extends BaseMapper<ChatBatch> {
}
