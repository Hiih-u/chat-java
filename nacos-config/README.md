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
```

---

## 上传步骤

1. 登录 Nacos 控制台：`http://192.168.202.155:8848/nacos`
2. 进入「配置管理」→「配置列表」
3. 点击右上角「+」新建配置
4. 依次创建上述 5 个 Data ID
5. 格式选择 **YAML**

## 动态刷新说明

以下配置类已添加 `@RefreshScope`，修改 Nacos 后**无需重启**即可生效：

| 模块 | 配置类 | 可热更新的配置项 |
|------|--------|----------------|
| chat-auth | [`JwtProperties`](../chat-auth/src/main/java/com/ai/chat/auth/config/JwtProperties.java) | `jwt.access-token-expire`、`jwt.refresh-token-expire` |
| chat-gateway | [`JwtProperties`](../chat-gateway/src/main/java/com/ai/chat/config/JwtProperties.java) | `jwt.secret` |
| chat-gateway | [`AuthProperties`](../chat-gateway/src/main/java/com/ai/chat/config/AuthProperties.java) | `auth.white-list`（白名单动态增减） |

**注意**：`spring.datasource` 数据库连接池配置**不支持**热更新，修改后需要重启服务。

## 注意事项

1. ⚠️ 生产环境务必修改 `jwt.secret` 为高强度随机密钥
2. ⚠️ 生产环境数据库密码建议使用 Nacos 配置加密或外部 KMS
3. `bootstrap.yml` 中的 Nacos 地址是服务启动必须的，不能放到 Nacos 里（鸡蛋问题）
4. `namespace` 填写的是命名空间的 **ID（UUID）**，不是显示名称，在 Nacos 控制台「命名空间」页面查看
