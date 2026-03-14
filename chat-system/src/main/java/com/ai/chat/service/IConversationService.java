package com.ai.chat.service;

import com.ai.chat.common.entity.Conversation;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


public interface IConversationService extends IService<Conversation> {

    Conversation createConversation(ConversationCreateRequest request);

    Conversation getByConversationId(String conversationId);

    Page<Conversation> pageQuery(int current, int size, String keyword);

    Conversation getDetails(String conversationIdOrId);

    Conversation updateConversation(Long id, ConversationUpdateRequest request);

    void deleteConversation(Long id);

    void batchDeleteConversation(Collection<Long> ids);

}
