package com.ai.chat.service;

import com.ai.chat.common.entity.Conversation;

/**
 * 会话服务接口
 */
public interface ConversationService {

    /**
     * 根据 conversationId 查询会话信息
     */
    Conversation getConversationById(String conversationId);

    /**
     * 创建新会话
     */
    Conversation createConversation(Conversation conversation);

    /**
     * 更新会话标题
     */
    void updateConversationTitle(String conversationId, String title);
}
