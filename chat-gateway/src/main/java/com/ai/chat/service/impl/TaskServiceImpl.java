package com.ai.chat.service.impl;

import com.ai.chat.common.entity.Task;
import com.ai.chat.common.mapper.TaskMapper;
import com.ai.chat.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskMapper taskMapper;
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public List<Task> getTasksByBatchId(String batchId) {
        log.info("查询 batchId: {} 的任务列表", batchId);
        return taskMapper.selectByBatchId(batchId);
    }

    @Override
    public Task getTaskByTaskId(String taskId) {
        log.info("查询 taskId: {} 的任务", taskId);
        return taskMapper.selectByTaskId(taskId);
    }

    @Override
    public List<Task> getTasksByConversationId(String conversationId) {
        log.info("查询 conversationId: {} 的历史任务", conversationId);
        return taskMapper.selectByConversationId(conversationId);
    }

    @Override
    public String testRedis(String key, String value) {
        log.info("测试 Redis 连接，设置 key: {}, value: {}", key, value);
        stringRedisTemplate.opsForValue().set(key, value);
        String result = stringRedisTemplate.opsForValue().get(key);
        log.info("从 Redis 读取到的值: {}", result);
        return result;
    }
}
