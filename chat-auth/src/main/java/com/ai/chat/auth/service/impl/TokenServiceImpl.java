package com.ai.chat.auth.service.impl;

import com.ai.chat.auth.mapper.UserTokenMapper;
import com.ai.chat.auth.pojo.entity.UserToken;
import com.ai.chat.auth.service.TokenService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserTokenMapper userTokenMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_CACHE_PREFIX = "auth:token:";
    private static final String USER_TOKEN_PREFIX = "auth:user:token:";

    @Override
    public void saveToken(UserToken userToken) {
        int result = userTokenMapper.insert(userToken);
        if (result <= 0) {
            throw new RuntimeException("保存令牌到数据库失败");
        }
        // 缓存访问令牌到 Redis
        cacheToken(userToken.getAccessToken(),userToken.getUserId(),userToken.getAccessExpireTime());

        // 缓存刷新令牌到 Redis
        if (userToken.getRefreshToken() != null) {
            cacheToken(userToken.getRefreshToken(),userToken.getUserId(),userToken.getRefreshExpireTime());
        }
        log.info("令牌保存成功，userId: {}, username: {}", userToken.getUserId(), userToken.getUsername());
    }

    @Override
    public String getTokenFromCache(String token) {
        String key = TOKEN_CACHE_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void deleteToken(String token) {
        LambdaQueryWrapper<UserToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserToken::getAccessToken, token)
                .or()
                .eq(UserToken::getRefreshToken, token);
        userTokenMapper.delete(wrapper);

        String key = TOKEN_CACHE_PREFIX + token;
        redisTemplate.delete(key);

        log.info("令牌删除成功，token: {}", token.substring(0, Math.min(20, token.length())));
    }

    @Override
    public void deleteTokenByUserId(Long userId) {
        LambdaQueryWrapper<UserToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserToken::getUserId, userId);
        List<UserToken> tokens = userTokenMapper.selectList(wrapper);

        for (UserToken token : tokens) {
            if (token.getAccessToken() != null) {
                redisTemplate.delete(TOKEN_CACHE_PREFIX + token.getAccessToken());
            }
            if(token.getRefreshToken() != null) {
                redisTemplate.delete(TOKEN_CACHE_PREFIX + token.getRefreshToken());
            }
        }

        userTokenMapper.delete(wrapper);

        log.info("用户所有令牌删除成功，userId: {}", userId);
    }

    @Override
    public boolean isTokenInCache(String token) {
        String key = TOKEN_CACHE_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    @Override
    public void cacheToken(String token, Long userId, LocalDateTime expireTime) {
        String key = TOKEN_CACHE_PREFIX + token;

        long ttl = Duration.between(LocalDateTime.now(), expireTime).getSeconds();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, userId.toString(), ttl, TimeUnit.SECONDS);
            log.debug("令牌缓存成功，userId: {}, ttl: {}秒", userId, ttl);
        } else {
            log.warn("令牌已过期，不进行缓存，userId: {}", userId);
        }
    }
}
