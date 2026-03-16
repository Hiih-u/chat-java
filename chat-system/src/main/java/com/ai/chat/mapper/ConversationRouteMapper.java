package com.ai.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ai.chat.pojo.entity.ConversationRoute;
import org.apache.ibatis.annotations.Mapper;

/**
 * ConversationRoute Mapper 接口
 * 用于 conversation_routes 表的数据访问
 * 实现会话粘性路由（Session Stickiness）
 */
@Mapper
public interface ConversationRouteMapper extends BaseMapper<ConversationRoute> {
}
