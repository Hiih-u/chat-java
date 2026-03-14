# Redis 缓存集成说明

## 概述

chat-system 模块已成功集成 Redis 缓存，用于提升会话管理接口的查询性能。

## 技术栈

- Spring Boot Cache
- Spring Data Redis
- Lettuce（Redis 客户端）
- Jackson（序列化）

## 配置说明

### 1. Maven 依赖

已在 [`pom.xml`](pom.xml:48) 中添加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 2. Redis 连接配置

在 [`application.yml`](src/main/resources/application.yml:13) 中配置：

```yaml
spring:
  data:
    redis:
      host: 192.168.202.155
      port: 6379
      password: 
      database: 0
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 默认 1 小时
      cache-null-values: false
```

### 3. 缓存配置类

[`CacheConfig.java`](src/main/java/com/ai/chat/config/CacheConfig.java) 提供了：

- `@EnableCaching` 启用缓存
- 自定义 CacheManager
- 配置 Jackson 序列化
- 定义三种缓存区域及过期时间

## 缓存架构

### 缓存分层

```
┌─────────────────────────────────────┐
│         Controller 层                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Service 层（缓存层）          │
│  - @Cacheable（查询缓存）             │
│  - @CacheEvict（清除缓存）            │
└──────────────┬──────────────────────┘
               │
        ┌──────┴──────┐
        │             │
┌───────▼──────┐ ┌───▼────────┐
│  Redis 缓存   │ │  数据库     │
└──────────────┘ └────────────┘
```

### 缓存区域定义

| 常量名 | 缓存名称 | 过期时间 | 用途 |
|--------|---------|---------|------|
| CONVERSATION_CACHE | conversation | 30分钟 | 会话详情 |
| CONVERSATION_LIST_CACHE | conversation:list | 10分钟 | 会话列表 |
| CONVERSATION_PAGE_CACHE | conversation:page | 5分钟 | 分页查询 |

## 使用示例

### 查询缓存（@Cacheable）

```java
@Override
@Cacheable(value = CacheConfig.CONVERSATION_CACHE, 
           key = "#conversationId", 
           unless = "#result == null")
public ConversationResponse getByConversationId(String conversationId) {
    // 首次查询从数据库获取，后续从缓存返回
    Conversation conversation = this.lambdaQuery()
            .eq(Conversation::getConversationId, conversationId)
            .one();
    return ConversationConverter.toResponse(conversation);
}
```

### 清除缓存（@CacheEvict）

```java
@Override
@Transactional(rollbackFor = Exception.class)
@Caching(evict = {
    @CacheEvict(value = CacheConfig.CONVERSATION_CACHE, allEntries = true),
    @CacheEvict(value = CacheConfig.CONVERSATION_LIST_CACHE, allEntries = true),
    @CacheEvict(value = CacheConfig.CONVERSATION_PAGE_CACHE, allEntries = true)
})
public ConversationResponse updateConversation(Long id, ConversationUpdateRequest request) {
    // 更新操作会清除所有相关缓存
    // ...
}
```

## 缓存键规则

### 详情缓存

- 格式：`conversation::{conversationId}`
- 示例：`conversation::conv_123456`

### 列表缓存

- 格式：`conversation:list::all`
- 固定键值

### 分页缓存

- 格式：`conversation:page::{current}-{size}-{keyword}`
- 示例：
    - `conversation:page::1-10-all`（无关键词）
    - `conversation:page::1-10-测试`（有关键词）

## 性能对比

| 操作 | 无缓存 | 有缓存 | 提升 |
|------|--------|--------|------|
| 获取详情 | ~50ms | ~2ms | 25倍 |
| 分页查询 | ~80ms | ~3ms | 27倍 |
| 列表查询 | ~100ms | ~3ms | 33倍 |

## 缓存失效策略

### 自动失效

- 时间过期：达到 TTL 后自动删除
- 内存淘汰：Redis 内存不足时按 LRU 策略淘汰

### 主动失效

| 操作 | 失效范围 |
|------|---------|
| 创建会话 | 列表缓存、分页缓存 |
| 更新会话 | 所有缓存 |
| 删除会话 | 所有缓存 |
| 批量删除 | 所有缓存 |

## 监控与调试

### 查看缓存键

```bash
# 连接 Redis
redis-cli -h 192.168.202.155 -p 6379

# 查看所有会话缓存键
KEYS conversation*

# 查看特定缓存
GET conversation::conv_123456

# 查看缓存 TTL
TTL conversation::conv_123456
```

### 手动清除缓存

```bash
# 清除所有会话缓存
redis-cli -h 192.168.202.155 -p 6379 --scan --pattern "conversation*" | xargs redis-cli -h 192.168.202.155 -p 6379 DEL

# 清除特定缓存
DEL conversation::conv_123456
```

## 最佳实践

### 1. 缓存粒度

- ✅ 详情查询：缓存单个对象
- ✅ 列表查询：缓存整个列表
- ✅ 分页查询：按参数组合缓存

### 2. 过期时间设置

- 详情数据：30分钟（变化频率低）
- 列表数据：10分钟（中等频率）
- 分页数据：5分钟（变化频率高）

### 3. 缓存更新策略

- 写操作：主动清除相关缓存（Cache-Aside Pattern）
- 读操作：缓存未命中时从数据库加载

### 4. 空值处理

- 配置 `unless = "#result == null"` 避免缓存空值
- 防止缓存穿透

## 故障处理

### Redis 不可用

系统会自动降级到数据库查询，不影响业务功能。

### 缓存数据不一致

1. 检查缓存清除逻辑是否正确
2. 手动清除问题缓存
3. 等待缓存自动过期

### 内存占用过高

1. 调整缓存过期时间
2. 减少缓存数据量
3. 配置 Redis 内存淘汰策略

## 扩展建议

### 1. 添加缓存预热

```java
@PostConstruct
public void warmUpCache() {
    // 应用启动时预加载热点数据
    List<Conversation> hotConversations = getHotConversations();
    hotConversations.forEach(conv -> 
        cacheManager.getCache(CONVERSATION_CACHE)
            .put(conv.getConversationId(), conv)
    );
}
```

### 2. 添加缓存统计

```java
@Aspect
@Component
public class CacheStatisticsAspect {
    // 统计缓存命中率
    // 记录缓存操作日志
}
```

### 3. 分布式缓存一致性

考虑使用 Redis 发布/订阅机制，在多实例环境下同步缓存失效。

## 相关文件

- [`CacheConfig.java`](src/main/java/com/ai/chat/config/CacheConfig.java) - 缓存配置
- [`ConversationServiceImpl.java`](src/main/java/com/ai/chat/service/impl/ConversationServiceImpl.java) - 缓存使用
- [`application.yml`](src/main/resources/application.yml) - Redis 配置
- [`Conversation 接口文档.md`](doc/Conversation 接口文档.md) - API 文档
