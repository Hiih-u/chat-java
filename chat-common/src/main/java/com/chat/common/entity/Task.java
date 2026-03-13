package com.chat.common.entity;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import com.chat.common.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "ai_tasks", autoResultMap = true)
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskId;
    private String batchId;
    private String conversationId;

    private String taskType;
    private String prompt;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> filePaths;

    private String responseText;
    private TaskStatus status;
    private String modelName;
    private String role;

    private Float costTime;
    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;
}
