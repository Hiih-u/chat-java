package com.chat.common.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 批次查询响应
 * 对应 Python 的 BatchQueryResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchQueryResponse {

    /**
     * 批次ID
     */
    private String batchId;

    /**
     * 批次状态：PROCESSING, COMPLETED, FAILED
     */
    private String status;

    /**
     * 用户原始提问
     */
    private String userPrompt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 该批次下所有模型的执行结果
     */
    private List<TaskSchema> results;
}
