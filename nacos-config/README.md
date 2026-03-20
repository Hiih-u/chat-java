# Nacos 配置中心说明

本目录存放需要上传到 Nacos 的配置文件内容，供参考和版本管理。

## 配置文件结构

```
Nacos 配置中心
├── DEFAULT_GROUP
│   ├── common-datasource.yaml   # 共享：数据库配置（chat-auth、chat-system 共用）
│   ├── common-redis.yaml        # 共享：Redis 配置（chat-auth、chat-system 共用）
│   ├── chat-auth.yaml           # 专属：chat-auth JWT 配置
│   ├── chat-system.yaml         # 专属：chat-system 专属配置（暂留空）
│   └── chat-gateway.yaml        # 专属：网关路由、白名单、JWT secret
```

## 配置加载顺序（优先级从高到低）

```
Nacos extension-configs（服务专属，如 chat-auth.yaml）
    ↓ 覆盖
Nacos shared-configs（共享，如 common-datasource.yaml）
    ↓ 覆盖
本地 application.yml
```

---

## 上传到 Nacos 的配置内容

### 1. `common-datasource.yaml`

> 命名空间：public（或指定 dev）  
> Group：DEFAULT_GROUP  
> Data ID：common-datasource.yaml

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.202.155:61020/postgres
    username: postgres
    password: Hi8899
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-timeout: 30000
```

---

### 2. `common-redis.yaml`

> Data ID：common-redis.yaml

```yaml
spring:
  data:
    redis:
      host: 192.168.202.155
      port: 61300
      password:
      database: 0
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0
```

---

### 3. `chat-auth.yaml`

> Data ID：chat-auth.yaml

```yaml
jwt:
  secret: your-256-bit-secret-key-for-jwt-token-generation-please-change-in-production
  access-token-expire: 7200      # 访问令牌过期时间（秒）2小时
  refresh-token-expire: 604800   # 刷新令牌过期时间（秒）7天
  issuer: chat-auth
```

---

### 4. `chat-system.yaml`

> Data ID：chat-system.yaml

```yaml
# chat-system 专属配置（目前暂无，可按需添加）
# 例如：
# spring:
#   cache:
#     redis:
#       time-to-live: 7200000
```

---

### 5. `chat-gateway.yaml`

> Data ID：chat-gateway.yaml

```yaml
jwt:
  secret: your-256-bit-secret-key-for-jwt-token-generation-please-change-in-production

auth:
  white-list:
    - /auth/login
    - /auth/register
    - /auth/captcha
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.202.155:8848
    gateway:
      discovery:
        locator:
          enabled: false
      routes:
        - id: chat-auth
          uri: lb://chat-auth
          predicates:
            - Path=/auth/**
        - id: chat-system
          uri: lb://chat-system
          predicates:
            - Path=/api/**

app:
  config:
    test-value: "hello-from-nacos2"
    description: "Nacos Config 动态刷新测试"
    feature-enabled: true
    rate-limit: 100    


# Actuator 暴露 refresh 端点（可手动触发刷新，也可用于验证）
management:
  endpoints:
    web:
      exposure:
        include: refresh,health,env
  endpoint:
    health:
      show-details: always

logging:
  level:
    org.springframework.cloud.gateway: TRACE
    com.ai.chat: DEUG
    # 开启 Nacos 配置刷新的 DEBUG 日志，方便排查热更新是否触发
    com.alibaba.cloud.nacos.refresh: DEBUG
    org.springframework.cloud.context.refresh: DEBUG

```

### 5. `common-actuator.yaml`

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,refresh
  endpoint:
    health:
      show-details: when-authorized
```