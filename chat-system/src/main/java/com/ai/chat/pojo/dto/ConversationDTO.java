package com.ai.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class ConversationDTO {

    @NotBlank(message = "conversationId 不能为空")
    private String conversationId;

    private String title;
    private Map<String, Object> sessionMetadata;
}
