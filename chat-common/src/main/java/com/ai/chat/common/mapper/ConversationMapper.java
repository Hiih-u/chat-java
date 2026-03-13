package com.ai.chat.common.mapper;

import com.ai.chat.common.entity.Conversation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    // 根据 conversationId 查询会话信息
    @Select("SELECT * FROM ai_conversations WHERE conversation_id = #{conversationId} LIMIT 1")
    Conversation selectByConversationId(@Param("conversationId") String conversationId);
}
