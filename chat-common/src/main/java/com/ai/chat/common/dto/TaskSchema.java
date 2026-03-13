package com.ai.chat.common.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 单个任务详情
 * 对应 Python 的 TaskSchema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSchema {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 任务状态：0-待处理, 1-成功, 2-失败, 3-处理中
     */
    private Integer status;

    /**
     * AI 响应文本
     */
    private String responseText;

    /**
     * 错误信息（如果失败）
     */
    private String errorMsg;

    /**
     * 耗时（秒）
     */
    private Float costTime;
}
