# Chat-Auth 认证模块使用指南

## 快速开始

### 1. 初始化数据库

在 PostgreSQL 中执行 SQL 脚本：

```bash
cd chat-java/chat-auth
psql -U postgres -d gemini -f src/main/resources/sql/schema.sql
```

这将创建：
- `sys_user` 表（用户表）
- `user_token` 表（令牌表）
- 测试用户：admin/123456 和 test/123456

### 2. 启动服务

**先启动 chat-system（端口 8081）：**
```bash
cd chat-java/chat-system
mvn spring-boot:run
```

**再启动 chat-auth（端口 8082）：**
```bash
cd chat-java/chat-auth
mvn spring-boot:run
```

### 3. 测试登录

```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456",
    "deviceType": "web"
  }'
```

成功响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userId": 1,
    "username": "admin"
  }
}
```

### 4. 使用令牌访问受保护接口

```bash
curl -X GET http://localhost:8082/auth/validate \
  -H "Authorization: Bearer {your-access-token}"
```

## 核心功能

### ✅ 已实现

1. **JWT 令牌认证**
    - 访问令牌（2小时有效期）
    - 刷新令牌（7天有效期）

2. **密码登录**
    - BCrypt 密码加密
    - 调用 chat-system 获取用户信息

3. **Token 双写**
    - Redis 缓存（快速验证）
    - PostgreSQL 持久化（审计和管理）

4. **Spring Security 集成**
    - JWT 过滤器
    - 路径白名单配置

5. **服务解耦**
    - 通过 Feign 调用 chat-system
    - 认证服务不直接查询用户表

## 架构说明

### 认证流程

```
1. 客户端 POST /auth/login {username, password}
2. chat-auth 调用 chat-system 获取用户信息
3. 验证密码（BCrypt）
4. 生成 JWT（access + refresh token）
5. Token 写入 Redis（设置过期时间）
6. Token 写入 PostgreSQL（持久化）
7. 返回 token 给客户端
```

### 服务依赖

```
chat-auth (8082)
    ↓ Feign
chat-system (8081)
    ↓
PostgreSQL (sys_user 表)

chat-auth
    ↓
Redis (token 缓存)
PostgreSQL (user_token 表)
```

## 配置说明

### JWT 配置

在 [`application.yml`](src/main/resources/application.yml:45) 中：

```yaml
jwt:
  secret: your-256-bit-secret-key  # ⚠️ 生产环境必须修改
  access-token-expire: 7200        # 2小时
  refresh-token-expire: 604800     # 7天
  issuer: chat-auth
```

### 白名单配置

```yaml
auth:
  permit-urls:
    - /auth/login      # 登录接口
    - /auth/register   # 注册接口（待实现）
    - /auth/captcha    # 验证码接口（待实现）
    - /error
    - /actuator/**
```

## API 接口

### 1. 登录
- **URL**: `POST /auth/login`
- **请求体**:
```json
{
  "username": "admin",
  "password": "123456",
  "deviceType": "web"
}
```

### 2. 登出
- **URL**: `POST /auth/logout`
- **请求头**: `Authorization: Bearer {token}`

### 3. 刷新令牌
- **URL**: `POST /auth/refresh?refreshToken={refreshToken}`

### 4. 验证令牌
- **URL**: `GET /auth/validate`
- **请求头**: `Authorization: Bearer {token}`

### 5. 健康检查
- **URL**: `GET /auth/health`

## 数据库表结构

### sys_user（用户表）
```sql
id              BIGSERIAL PRIMARY KEY
username        VARCHAR(50) UNIQUE NOT NULL
password        VARCHAR(200) NOT NULL  -- BCrypt 加密
phone           VARCHAR(20)
email           VARCHAR(100)
real_name       VARCHAR(50)
status          SMALLINT DEFAULT 1     -- 1:正常 0:禁用
tenant_id       BIGINT
created_at      TIMESTAMP
updated_at      TIMESTAMP
deleted         SMALLINT DEFAULT 0
```

### user_token（令牌表）
```sql
id                    BIGSERIAL PRIMARY KEY
user_id               BIGINT NOT NULL
username              VARCHAR(50) NOT NULL
access_token          VARCHAR(500) NOT NULL
refresh_token         VARCHAR(500)
access_expire_time    TIMESTAMP NOT NULL
refresh_expire_time   TIMESTAMP
login_ip              VARCHAR(50)
user_agent            VARCHAR(500)
device_type           VARCHAR(20)
created_at            TIMESTAMP
updated_at            TIMESTAMP
deleted               SMALLINT DEFAULT 0
```

## 关键类说明

### 核心类

| 类名 | 说明 |
|------|------|
| [`AuthApplication`](src/main/java/com/ai/chat/auth/AuthApplication.java:1) | 启动类 |
| [`AuthController`](src/main/java/com/ai/chat/auth/controller/AuthController.java:1) | 认证接口 |
| [`AuthService`](src/main/java/com/ai/chat/auth/service/AuthService.java:1) | 认证服务 |
| [`TokenService`](src/main/java/com/ai/chat/auth/service/TokenService.java:1) | 令牌服务 |
| [`JwtUtil`](src/main/java/com/ai/chat/auth/util/JwtUtil.java:1) | JWT 工具类 |
| [`SecurityConfig`](src/main/java/com/ai/chat/auth/config/SecurityConfig.java:1) | Security 配置 |
| [`JwtAuthenticationFilter`](src/main/java/com/ai/chat/auth/filter/JwtAuthenticationFilter.java:1) | JWT 过滤器 |
| [`UserClient`](src/main/java/com/ai/chat/auth/client/UserClient.java:1) | Feign 客户端 |

## 常见问题

### Q1: 登录失败提示"用户服务暂时不可用"
**A**: 确保 chat-system 服务已启动，并且 Feign 配置的 URL 正确。

### Q2: Token 验证失败
**A**: 检查：
1. Token 是否过期
2. Redis 是否正常运行
3. JWT secret 是否一致

### Q3: 数据库连接失败
**A**: 检查 application.yml 中的数据库配置是否正确。

## 后续扩展计划

- [ ] 图形验证码登录
- [ ] 短信验证码登录
- [ ] 登录失败次数限制（5次锁定30分钟）
- [ ] IP 锁定机制
- [ ] 在线用户管理
- [ ] 踢人下线功能
- [ ] 第三方社交登录（微信、QQ等）
- [ ] 多租户支持

## 注意事项

1. ⚠️ **生产环境必须修改 JWT secret**
2. ⚠️ **确保 Redis 和 PostgreSQL 可访问**
3. ⚠️ **Token 缓存和持久化必须同时成功**
4. ⚠️ **chat-system 必须先启动**
