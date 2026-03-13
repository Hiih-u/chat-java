package com.ai.chat.common.dto;

import com.ai.chat.common.entity.Task;
import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class BatchSubmitResponse {
    private String batchId;
    private String conversationId;
    private String message;
    private List<Task> taskIds;
}
