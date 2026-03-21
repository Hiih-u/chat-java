package com.ai.chat.wrapper;

import com.ai.chat.system.pojo.entity.Conversation;
import com.ai.chat.system.pojo.dto.ConversationDTO;
import com.ai.chat.system.pojo.vo.ConversationVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Conversation DTO 转换工具类
 */
public class ConversationWrapper {

    /**
     * Entity 转 Response DTO
     */
    public static ConversationVo toResponse(Conversation entity) {
        if (entity == null) {
            return null;
        }
        ConversationVo response = new ConversationVo();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    /**
     * Entity 列表转 Response DTO 列表
     */
    public static List<ConversationVo> toResponseList(List<Conversation> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ConversationWrapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * ConversationDTO 转 Entity
     */
    public static Conversation toEntity(ConversationDTO dto) {
        if (dto == null) {
            return null;
        }
        Conversation entity = new Conversation();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
