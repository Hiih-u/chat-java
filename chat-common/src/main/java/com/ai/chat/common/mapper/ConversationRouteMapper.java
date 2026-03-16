package com.ai.chat.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ai.chat.common.pojo.entity.ConversationRoute;
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
}
