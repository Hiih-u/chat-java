package com.ai.chat.service;

import com.ai.chat.common.entity.Task;
import com.ai.chat.common.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务服务类
 * 用于测试数据库和 Redis 连接
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 根据 batchId 查询任务列表
     */
    public List<Task> getTasksByBatchId(String batchId) {
        log.info("查询 batchId: {} 的任务列表", batchId);
        return taskMapper.selectByBatchId(batchId);
    }

    /**
     * 根据 taskId 查询单个任务
     */
    public Task getTaskByTaskId(String taskId) {
        log.info("查询 taskId: {} 的任务", taskId);
        return taskMapper.selectByTaskId(taskId);
    }

    /**
     * 测试 Redis 连接
     */
    public String testRedis(String key, String value) {
        log.info("测试 Redis 连接，设置 key: {}, value: {}", key, value);
        stringRedisTemplate.opsForValue().set(key, value);
        String result = stringRedisTemplate.opsForValue().get(key);
        log.info("从 Redis 读取到的值: {}", result);
        return result;
    }
}
