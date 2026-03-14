package com.ai.chat.service;

import com.ai.chat.common.entity.Conversation;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.request.ConversationUpdateRequest;
import com.ai.chat.dto.response.ConversationResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;


public interface IConversationService extends IService<Conversation> {

    ConversationResponse createConversation(ConversationCreateRequest request);

    ConversationResponse getByConversationId(String conversationId);

    Page<ConversationResponse> pageQuery(int current, int size, String keyword);

    ConversationResponse getDetails(String conversationId);

    ConversationResponse updateConversation(Long id, ConversationUpdateRequest request);

    void deleteConversation(Long id);

    void batchDeleteConversation(Collection<Long> ids);

    List<ConversationResponse> listAll();

}
