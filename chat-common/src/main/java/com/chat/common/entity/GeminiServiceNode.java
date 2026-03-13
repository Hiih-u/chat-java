package com.chat.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("gemini_service_nodes")
public class GeminiServiceNode {

    @TableId(type = IdType.INPUT)
    private String nodeUrl;

    private String workerId;
    private String status;
    private Integer dispatchedTasks;
    private Integer currentTasks;
    private Float weight;
    private Integer errorCount;

    private LocalDateTime lastHeartbeat;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}