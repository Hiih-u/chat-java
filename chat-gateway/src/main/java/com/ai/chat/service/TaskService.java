package com.ai.chat.service;

import com.ai.chat.common.entity.Task;

import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {

    /**
     * 根据 batchId 查询任务列表
     */
    List<Task> getTasksByBatchId(String batchId);

    /**
     * 根据 taskId 查询单个任务
     */
    Task getTaskByTaskId(String taskId);

    /**
     * 根据 conversationId 查询历史任务
     */
    List<Task> getTasksByConversationId(String conversationId);

    /**
     * 测试 Redis 连接
     */
    String testRedis(String key, String value);
}
