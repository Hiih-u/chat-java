package com.ai.chat.converter;

import com.ai.chat.common.entity.Conversation;
import com.ai.chat.dto.request.ConversationCreateRequest;
import com.ai.chat.dto.response.ConversationResponse;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Conversation DTO 转换工具类
 */
public class ConversationConverter {

    /**
     * Entity 转 Response DTO
     */
    public static ConversationResponse toResponse(Conversation entity) {
        if (entity == null) {
            return null;
        }
        ConversationResponse response = new ConversationResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    /**
     * Entity 列表转 Response DTO 列表
     */
    public static List<ConversationResponse> toResponseList(List<Conversation> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ConversationConverter::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * CreateRequest 转 Entity
     */
    public static Conversation toEntity(ConversationCreateRequest request) {
        if (request == null) {
            return null;
        }
        Conversation entity = new Conversation();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }
}
