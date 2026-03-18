package com.ai.chat.auth.service;

import com.ai.chat.auth.pojo.entity.UserToken;

import java.time.LocalDateTime;

/**
 * 令牌服务接口
 */
public interface TokenService {

    /**
     * 保存令牌到数据库和 Redis
     */
    void saveToken(UserToken userToken);

    /**
     * 从 Redis 获取令牌
     */
    String getTokenFromCache(String token);

    /**
     * 删除令牌（数据库和 Redis）
     */
    void deleteToken(String token);

    /**
     * 根据用户ID删除所有令牌
     */
    void deleteTokenByUserId(Long userId);

    /**
     * 验证令牌是否在缓存中
     */
    boolean isTokenInCache(String token);

    /**
     * 缓存令牌到 Redis
     */
    void cacheToken(String token, Long userId, LocalDateTime expireTime);
}
