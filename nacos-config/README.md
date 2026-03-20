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
    - /gateway/config   # Nacos 动态刷新测试接口（无需 Token）

# =====================================================
# Nacos Config 动态刷新测试配置（app.config.*）
# 修改以下任意字段后点击「发布」，无需重启即可热更新。
# 通过 GET /gateway/config/current 或 /gateway/config/test-props 验证。
# =====================================================
app:
  config:
    test-value: "hello-from-nacos"       # 随意修改此值来验证热更新
    description: "Nacos Config 动态刷新测试"
    feature-enabled: false               # 功能开关，修改为 true 观察变化
    rate-limit: 100                      # 限流阈值示例

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
| chat-gateway | [`NacosConfigTestProperties`](../chat-gateway/src/main/java/com/ai/chat/config/NacosConfigTestProperties.java) | `app.config.*` 所有字段 |

**注意**：`spring.datasource` 数据库连接池配置**不支持**热更新，修改后需要重启服务。

---

## 热更新失效排查指南

### 已知陷阱及修复

#### 陷阱1：`@Component` + `@ConfigurationProperties` + `@RefreshScope` 三注解共用（❌ 错误写法）

```java
// ❌ 错误：三注解共用导致 Bean 双重注册
@Data
@Component          // 让 Spring 直接扫描注册
@RefreshScope
@ConfigurationProperties(prefix = "app.config")
public class NacosConfigTestProperties { ... }
```

**原因**：Spring Boot 的 `ConfigurationPropertiesBindingPostProcessor` 会将属性绑定到「原始 Bean」，
而 `@RefreshScope` 让容器中实际注入的是「CGLIB 代理 Bean」。
刷新时代理 Bean 被销毁重建，但新建的代理 Bean 属性未重新绑定，读到的仍是旧值或默认值。

```java
// ✅ 正确：去掉 @Component，由 @EnableConfigurationProperties 统一注册
@Data
@RefreshScope
@ConfigurationProperties(prefix = "app.config")
public class NacosConfigTestProperties { ... }
```

在启动类统一声明：

```java
@SpringBootApplication
@EnableConfigurationProperties({
    JwtProperties.class,
    AuthProperties.class,
    NacosConfigTestProperties.class
})
public class GatewayApplication { ... }
```

#### 陷阱2：`spring-cloud-starter-bootstrap` 与 `spring.config.import` 混用（❌ 错误写法）

```xml
<!-- ❌ 不要同时引入 bootstrap 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

**原因**：`spring-cloud-starter-bootstrap` 是 Spring Cloud 2020 之前的老方式（需配合 `bootstrap.yml`）。
项目同时使用了新方式（`spring.config.import: nacos:...`），两套机制共存时，
Nacos 刷新监听器会注册两次，事件链路混乱，导致 `@RefreshScope` Bean 不刷新。

**修复**：移除 `bootstrap` 依赖，仅保留 `spring.config.import` 新方式。

#### 陷阱3：缺少 `spring-boot-starter-actuator`（❌ 缺失依赖）

Nacos 配置变更推送的 `RefreshEvent` 需要 actuator 的 refresh 机制作为触发链路，
缺少 actuator 时，即使 Nacos 推送了变更，`@RefreshScope` Bean 也不会重建。

```xml
<!-- ✅ 必须添加 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 陷阱4：`spring.config.import` 中缺少 `refresh=true`

```yaml
# ❌ 缺少 refresh=true，Nacos 客户端不会监听变更
spring:
  config:
    import:
      - nacos:chat-gateway.yaml?group=DEFAULT_GROUP

# ✅ 正确：加上 refresh=true
spring:
  config:
    import:
      - nacos:chat-gateway.yaml?group=DEFAULT_GROUP&refresh=true
```

### 验证热更新是否触发

修改 Nacos 配置后，观察网关日志，若看到以下内容则说明刷新正常触发：

```
INFO  c.a.c.n.refresh.NacosContextRefresher  - Refresh Nacos config group=DEFAULT_GROUP, dataId=chat-gateway.yaml
INFO  o.s.c.c.ConfigurationPropertiesRebinder - Properties rebinding: authProperties
INFO  o.s.c.c.ConfigurationPropertiesRebinder - Properties rebinding: jwtProperties
```

若日志中没有上述输出，检查：
1. Nacos 控制台是否成功发布（版本号有无变化）
2. 网关能否访问 Nacos（`8848` 端口是否通）
3. `application.yml` 中 `spring.cloud.refresh.enabled` 是否为 `true`

---

## 动态刷新测试接口

网关提供了两个无需 Token 的测试接口（已加入白名单），可直接访问：

### 接口 1：查看所有动态配置

```
GET http://localhost:8000/gateway/config/current
```

**响应示例：**

```json
{
  "queriedAt": "2026-03-20 09:00:00",
  "message": "以下值来自 Nacos，修改 chat-gateway.yaml 后无需重启即可刷新",
  "appConfig": {
    "testValue": "hello-from-nacos",
    "description": "Nacos Config 动态刷新测试",
    "featureEnabled": false,
    "rateLimit": 100
  },
  "authWhiteList": [
    "/auth/login",
    "/auth/register",
    "/auth/captcha",
    "/gateway/config"
  ],
  "jwtSecretPreview": "your-2***"
}
```

### 接口 2：仅查看 app.config 测试属性

```
GET http://localhost:8000/gateway/config/test-props
```

**响应示例：**

```json
{
  "testValue": "hello-from-nacos",
  "description": "Nacos Config 动态刷新测试",
  "featureEnabled": false,
  "rateLimit": 100,
  "tip": "修改 Nacos chat-gateway.yaml 中 app.config.* 后，此值将实时更新"
}
```

### 测试步骤

1. 启动网关服务（确保 Nacos 运行中）
2. 将上方 `chat-gateway.yaml` 内容上传到 Nacos
3. 访问 `GET /gateway/config/test-props`，记录 `testValue` 当前值
4. 登录 Nacos 控制台，编辑 `chat-gateway.yaml`，修改 `app.config.test-value` 为任意新值，点击「发布」
5. **等待约 1~3 秒**，再次访问 `GET /gateway/config/test-props`
6. 观察 `testValue` 是否已变为新值 —— 若是，则证明动态刷新生效 ✓

> **原理说明**：`NacosConfigTestProperties`、`JwtProperties`、`AuthProperties` 均标注了
> `@RefreshScope`，Spring Cloud 会在 Nacos 推送配置变更事件后，销毁并重建这些 Bean，
> 使新配置自动生效，无需重启 JVM。

## 注意事项

1. ⚠️ 生产环境务必修改 `jwt.secret` 为高强度随机密钥
2. ⚠️ 生产环境数据库密码建议使用 Nacos 配置加密或外部 KMS
3. `bootstrap.yml` 中的 Nacos 地址是服务启动必须的，不能放到 Nacos 里（鸡蛋问题）
4. `namespace` 填写的是命名空间的 **ID（UUID）**，不是显示名称，在 Nacos 控制台「命名空间」页面查看
5. `/gateway/config/**` 路径已加入白名单，**仅用于开发测试，生产环境建议移除**
