package com.ai.chat.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "ai_conversations", autoResultMap = true)
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String conversationId;
    private String title;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> sessionMetadata;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
