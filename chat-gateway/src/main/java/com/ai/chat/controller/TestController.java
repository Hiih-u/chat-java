package com.ai.chat.controller;

import com.ai.chat.service.TaskService;
import com.ai.chat.common.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试数据库和 Redis 连接
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TaskService taskService;

    /**
     * 测试数据库连接 - 查询任务
     */
    @GetMapping("/db/tasks/{batchId}")
    public Map<String, Object> testDatabase(@PathVariable String batchId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Task> tasks = taskService.getTasksByBatchId(batchId);
            result.put("success", true);
            result.put("count", tasks.size());
            result.put("tasks", tasks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试 Redis 连接
     */
    @GetMapping("/redis")
    public Map<String, Object> testRedis(
            @RequestParam(defaultValue = "test-key") String key,
            @RequestParam(defaultValue = "test-value") String value) {
        Map<String, Object> result = new HashMap<>();
        try {
            String redisValue = taskService.testRedis(key, value);
            result.put("success", true);
            result.put("key", key);
            result.put("value", redisValue);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
