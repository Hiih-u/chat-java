package com.ai.chat.service.impl;

import com.ai.chat.service.ConversationService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ai.chat.common.entity.Conversation;
import com.ai.chat.common.mapper.ConversationMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 会话服务实现类
 */
@Slf4j
@Service
@AllArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private ConversationMapper conversationMapper;

    @Override
    public Conversation getConversationById(String conversationId) {
        log.info("查询 conversationId: {} 的会话信息", conversationId);
        return conversationMapper.selectByConversationId(conversationId);
    }

    @Override
    public Conversation createConversation(Conversation conversation) {
        log.info("创建新会话: {}", conversation.getConversationId());
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public void updateConversationTitle(String conversationId, String title) {
        log.info("更新会话 {} 的标题为: {}", conversationId, title);
        LambdaUpdateWrapper<Conversation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Conversation::getConversationId, conversationId)
                .set(Conversation::getTitle, title);
        conversationMapper.update(null, updateWrapper);
    }
}
