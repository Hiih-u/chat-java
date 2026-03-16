package com.ai.chat.common.pojo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ConversationUpdateDTO {

    private String title;
    private Map<String, Object> sessionMetadata;
}
