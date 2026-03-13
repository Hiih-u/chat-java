package com.chat.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskQueryResponse {
    private String taskId;
    private String conversationId;
    private Integer status;
    private String taskType;
    private String prompt;
    private LocalDateTime createdAt;
    private Float costTime;
    private String responseText;
    private String modelName;
}
