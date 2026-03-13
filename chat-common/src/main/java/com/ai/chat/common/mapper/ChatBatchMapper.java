package com.ai.chat.common.mapper;

import com.ai.chat.common.entity.ChatBatch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChatBatchMapper extends BaseMapper<ChatBatch> {

    // 根据 batchId 查询批次信息
    @Select("SELECT * FROM chat_batches WHERE batch_id = #{batchId} LIMIT 1")
    ChatBatch selectByBatchId(@Param("batchId") String batchId);

    // 根据 conversationId 获取批次信息
    @Select("SELECT * FROM chat_batches WHERE conversation_id = #{conversationId} ORDER BY created_at DESC LIMIT 1")
    ChatBatch selectByConversationId(@Param("conversationId") String conversationId);
}
