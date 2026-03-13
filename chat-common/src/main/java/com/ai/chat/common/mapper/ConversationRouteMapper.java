package com.ai.chat.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ai.chat.common.entity.ConversationRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * ConversationRoute Mapper 接口
 * 用于 conversation_routes 表的数据访问
 * 实现会话粘性路由（Session Stickiness）
 */
@Mapper
public interface ConversationRouteMapper extends BaseMapper<ConversationRoute> {

    /**
     * 根据 conversationId 查询路由信息
     */
    @Select("SELECT * FROM conversation_routes WHERE conversation_id = #{conversationId} LIMIT 1")
    ConversationRoute selectByConversationId(@Param("conversationId") String conversationId);
}
