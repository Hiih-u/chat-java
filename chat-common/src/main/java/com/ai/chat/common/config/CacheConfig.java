package com.ai.chat.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置类
 */
@Profile("!test")
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 缓存名称常量
     */
    public static final String CONVERSATION_CACHE = "conversation";
    public static final String CONVERSATION_LIST_CACHE = "conversation:list";
    public static final String CONVERSATION_PAGE_CACHE = "conversation:page";

    /**
     * 配置缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 默认缓存配置：1小时过期
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        // 针对不同缓存的个性化配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 会话详情缓存：30分钟
        cacheConfigurations.put(CONVERSATION_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 会话列表缓存：10分钟
        cacheConfigurations.put(CONVERSATION_LIST_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // 会话分页缓存：5分钟
        cacheConfigurations.put(CONVERSATION_PAGE_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
