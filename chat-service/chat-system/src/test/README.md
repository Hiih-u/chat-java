# Conversation 测试说明

## 测试结构

```
src/test/java/
├── unit/                          # 单元测试（占 70%）
│   ├── controller/
│   │   └── ConversationControllerTest.java      (@WebMvcTest)
│   └── service/
│       └── ConversationServiceTest.java         (@ExtendWith(MockitoExtension))
│
└── integration/                   # 集成测试（占 30%）
    └── ConversationIntegrationTest.java         (@SpringBootTest)
```

## 测试类型说明

### 1. Controller 单元测试 (unit/controller)
- **注解**: `@WebMvcTest(ConversationController.class)`
- **目的**: 只测试 Web 层，Mock Service 层
- **特点**: 
  - 快速执行
  - 隔离测试 HTTP 请求/响应
  - 验证参数校验、异常处理、返回格式
- **覆盖场景**:
  - 所有 API 端点的正常流程
  - 参数校验失败场景
  - 业务异常处理
  - HTTP 状态码验证

### 2. Service 单元测试 (unit/service)
- **注解**: `@ExtendWith(MockitoExtension.class)`
- **目的**: 纯单元测试，Mock Mapper 层
- **特点**:
  - 最快的测试执行速度
  - 完全隔离的业务逻辑测试
  - 不依赖数据库和 Spring 容器
- **覆盖场景**:
  - 所有业务方法的正常流程
  - 边界条件测试
  - 异常场景测试
  - 数据转换逻辑验证

### 3. 集成测试 (integration)
- **注解**: `@SpringBootTest` + `@Transactional`
- **目的**: 测试完整业务流程和真实数据库交互
- **特点**:
  - 启动完整 Spring 容器
  - 使用 H2 内存数据库
  - 每个测试自动回滚
  - Mock Redis 连接
- **覆盖场景**:
  - 完整业务流程测试
  - 数据库事务验证
  - 分页和查询功能
  - JSON 序列化/反序列化
  - 并发场景模拟

## 测试配置

### application-test.yml
- H2 内存数据库配置
- 自动加载 schema-test.sql
- 关闭 Redis 缓存
- 启用 SQL 日志

### schema-test.sql
- 创建测试表结构
- 兼容 PostgreSQL 语法
- 支持自增主键和唯一索引

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 只运行单元测试
```bash
mvn test -Dtest="unit/**/*Test"
```

### 只运行集成测试
```bash
mvn test -Dtest="integration/**/*Test"
```

### 运行特定测试类
```bash
mvn test -Dtest=ConversationControllerTest
```

## 测试覆盖率

- **Controller 层**: 100% 覆盖所有 API 端点
- **Service 层**: 100% 覆盖所有业务方法
- **集成测试**: 覆盖核心业务流程和边界场景

## 注意事项

1. **旧测试文件**: `com.ai.chat.controller` 和 `com.ai.chat.service` 包下的旧测试文件已被新结构替代，可以删除
2. **测试隔离**: 所有集成测试使用 `@Transactional` 确保数据隔离
3. **Mock Redis**: 测试环境通过 `@MockBean` 屏蔽 Redis 连接
4. **断言库**: 使用 AssertJ 提供更流畅的断言语法
