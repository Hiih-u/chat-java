package com.ai.chat.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("chat_batches")
public class ChatBatch {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String batchId;
    private String conversationId;
    private String userPrompt;
    private String modelConfig;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<Task> tasks;
}
