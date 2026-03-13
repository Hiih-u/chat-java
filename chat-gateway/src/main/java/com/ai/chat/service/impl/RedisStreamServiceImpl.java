package com.ai.chat.service.impl;

import com.ai.chat.service.RedisStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Redis Stream 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamServiceImpl implements RedisStreamService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String sendMessage(String streamKey, Map<String, String> message) {
        log.info("向 Redis Stream {} 发送消息: {}", streamKey, message);

        MapRecord<String, String, String> record = StreamRecords
                .newRecord()
                .ofStrings(message)
                .withStreamKey(streamKey);

        var recordId = stringRedisTemplate.opsForStream().add(record);
        log.info("消息已发送，ID: {}", recordId);

        return recordId != null ? recordId.getValue() : null;
    }

    @Override
    public String testConnection(String key, String value) {
        log.info("测试 Redis 连接，设置 key: {}, value: {}", key, value);
        stringRedisTemplate.opsForValue().set(key, value);
        String result = stringRedisTemplate.opsForValue().get(key);
        log.info("从 Redis 读取到的值: {}", result);
        return result;
    }
}
