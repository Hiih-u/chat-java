package com.ai.chat.service.impl;

import com.ai.chat.service.BatchService;
import com.ai.chat.common.entity.ChatBatch;
import com.ai.chat.common.mapper.ChatBatchMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 批次服务实现类
 */
@Slf4j
@Service
@AllArgsConstructor
public class BatchServiceImpl implements BatchService {

    private ChatBatchMapper chatBatchMapper;

    @Override
    public ChatBatch getBatchById(String batchId) {
        log.info("查询 batchId: {} 的批次信息", batchId);
        return chatBatchMapper.selectByBatchId(batchId);
    }

    @Override
    public ChatBatch getLatestBatchByConversationId(String conversationId) {
        log.info("查询 conversationId: {} 的最新批次", conversationId);
        return chatBatchMapper.selectByConversationId(conversationId);
    }

    @Override
    public ChatBatch createBatch(ChatBatch batch) {
        log.info("创建新批次: {}", batch.getBatchId());
        chatBatchMapper.insert(batch);
        return batch;
    }
}
