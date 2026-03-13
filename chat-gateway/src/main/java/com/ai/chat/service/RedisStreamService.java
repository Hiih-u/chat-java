package com.ai.chat.service;

import java.util.Map;

/**
 * Redis 服务接口
 * 用于 Redis Streams 任务分发
 */
public interface RedisStreamService {

    /**
     * 向 Redis Stream 发送消息
     * @param streamKey Stream 名称（如 gemini_stream, deepseek_stream）
     * @param message 消息内容
     * @return 消息 ID
     */
    String sendMessage(String streamKey, Map<String, String> message);

    /**
     * 测试 Redis 连接
     */
    String testConnection(String key, String value);
}
