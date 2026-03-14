package com.ai.chat.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ConversationResponse {

    private Long id;
    private String conversationId;
    private String title;
    private Map<String, Object> sessionMetadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
