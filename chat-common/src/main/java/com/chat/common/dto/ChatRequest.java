package com.chat.common.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ChatRequest {

    @NotBlank(message = "prompt 不能为空")
    private String prompt;

    private String model;
    private String conversationId;
}
