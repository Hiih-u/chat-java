package com.ai.chat.service;

import com.ai.chat.common.entity.ChatBatch;

/**
 * 批次服务接口
 */
public interface BatchService {

    /**
     * 根据 batchId 查询批次信息
     */
    ChatBatch getBatchById(String batchId);

    /**
     * 根据 conversationId 查询最新批次
     */
    ChatBatch getLatestBatchByConversationId(String conversationId);

    /**
     * 创建新批次
     */
    ChatBatch createBatch(ChatBatch batch);
}
