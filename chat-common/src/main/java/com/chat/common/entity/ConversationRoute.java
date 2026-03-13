package com.chat.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("conversation_routes")
public class ConversationRoute {

    @TableId(type = IdType.INPUT)
    private String conversationId;

    @TableId(type = IdType.INPUT)
    private Integer slotId;

    private String nodeUrl;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
