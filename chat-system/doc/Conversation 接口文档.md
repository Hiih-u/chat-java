# 会话管理 API 文档

## 基础信息

- **Base URL**: `/api/conversation`
- **Content-Type**: `application/json`

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 响应码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 接口列表

### 1. 创建会话

**接口地址**: `POST /api/conversation/create`

**请求参数**:

```json
{
  "conversationId": "conv_123456",
  "title": "我的会话",
  "sessionMetadata": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| conversationId | String | 是 | 会话唯一标识 |
| title | String | 否 | 会话标题 |
| sessionMetadata | Map | 否 | 会话元数据 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "conversationId": "conv_123456",
    "title": "我的会话",
    "sessionMetadata": {
      "key1": "value1",
      "key2": "value2"
    },
    "createdAt": "2026-03-14T14:30:00",
    "updatedAt": "2026-03-14T14:30:00"
  }
}
```

---

### 2. 获取会话详情

**接口地址**: `GET /api/conversation/{conversationId}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| conversationId | String | 是 | 会话ID或数据库主键ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "conversationId": "conv_123456",
    "title": "我的会话",
    "sessionMetadata": {
      "key1": "value1"
    },
    "createdAt": "2026-03-14T14:30:00",
    "updatedAt": "2026-03-14T14:30:00"
  }
}
```

---

### 3. 分页查询会话列表

**接口地址**: `GET /api/conversation/page`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| current | Integer | 否 | 1 | 当前页码 |
| size | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词（支持标题和conversationId模糊查询） |

**请求示例**:

```
GET /api/conversation/page?current=1&size=10&keyword=测试
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "conversationId": "conv_123456",
        "title": "我的会话",
        "sessionMetadata": {},
        "createdAt": "2026-03-14T14:30:00",
        "updatedAt": "2026-03-14T14:30:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 4. 查询所有会话

**接口地址**: `GET /api/conversation/list`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "conversationId": "conv_123456",
      "title": "我的会话",
      "sessionMetadata": {},
      "createdAt": "2026-03-14T14:30:00",
      "updatedAt": "2026-03-14T14:30:00"
    },
    {
      "id": 2,
      "conversationId": "conv_789012",
      "title": "另一个会话",
      "sessionMetadata": {},
      "createdAt": "2026-03-14T15:00:00",
      "updatedAt": "2026-03-14T15:00:00"
    }
  ]
}
```

---

### 5. 更新会话

**接口地址**: `PUT /api/conversation/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 会话数据库主键ID |

**请求参数**:

```json
{
  "title": "更新后的标题",
  "sessionMetadata": {
    "newKey": "newValue"
  }
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 否 | 会话标题 |
| sessionMetadata | Map | 否 | 会话元数据 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "conversationId": "conv_123456",
    "title": "更新后的标题",
    "sessionMetadata": {
      "newKey": "newValue"
    },
    "createdAt": "2026-03-14T14:30:00",
    "updatedAt": "2026-03-14T16:00:00"
  }
}
```

---

### 6. 删除会话

**接口地址**: `DELETE /api/conversation/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 会话数据库主键ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 7. 批量删除会话

**接口地址**: `DELETE /api/conversation/batch`

**请求参数**:

```json
[1, 2, 3, 4, 5]
```

**参数说明**: 会话ID数组（Long类型）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 错误响应示例

### 参数错误

```json
{
  "code": 400,
  "message": "conversationId 不能为空",
  "data": null
}
```

### 资源不存在

```json
{
  "code": 404,
  "message": "会话不存在",
  "data": null
}
```

### 服务器错误

```json
{
  "code": 500,
  "message": "创建会话失败",
  "data": null
}
```

---

## 数据模型

### ConversationResponse

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 数据库主键ID |
| conversationId | String | 会话唯一标识 |
| title | String | 会话标题 |
| sessionMetadata | Map<String, Object> | 会话元数据 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

### ConversationCreateRequest

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| conversationId | String | 是 | 会话唯一标识 |
| title | String | 否 | 会话标题 |
| sessionMetadata | Map<String, Object> | 否 | 会话元数据 |

### ConversationUpdateRequest

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 否 | 会话标题 |
| sessionMetadata | Map<String, Object> | 否 | 会话元数据 |

---

## Redis 缓存策略

本系统已集成 Redis 缓存，提升查询性能。

### 缓存配置

| 缓存名称 | 缓存键 | 过期时间 | 说明 |
|---------|--------|---------|------|
| conversation | conversationId 或 id | 30分钟 | 会话详情缓存 |
| conversation:list | 'all' | 10分钟 | 会话列表缓存 |
| conversation:page | current-size-keyword | 5分钟 | 分页查询缓存 |

### 缓存策略说明

#### 查询接口（使用缓存）

- **获取会话详情** (`GET /{conversationId}`)
    - 缓存键：`conversation::{conversationId}`
    - 首次查询从数据库获取，后续查询直接从缓存返回
    - 30分钟后自动过期

- **查询所有会话** (`GET /list`)
    - 缓存键：`conversation:list::all`
    - 10分钟后自动过期

- **分页查询** (`GET /page`)
    - 缓存键：`conversation:page::{current}-{size}-{keyword}`
    - 不同的分页参数和关键词会生成不同的缓存
    - 5分钟后自动过期

#### 写操作（清除缓存）

- **创建会话** (`POST /create`)
    - 清除：列表缓存、分页缓存
    - 保留：已有的详情缓存（不受影响）

- **更新会话** (`PUT /{id}`)
    - 清除：所有缓存（详情、列表、分页）
    - 确保数据一致性

- **删除会话** (`DELETE /{id}`)
    - 清除：所有缓存（详情、列表、分页）

- **批量删除** (`DELETE /batch`)
    - 清除：所有缓存（详情、列表、分页）

### 缓存优势

1. **性能提升**：查询接口响应时间从数据库查询的几十毫秒降低到 1-2 毫秒
2. **减轻数据库压力**：高频查询直接从 Redis 返回，降低数据库负载
3. **智能失效**：写操作自动清除相关缓存，保证数据一致性
4. **分级过期**：不同类型的数据设置不同的过期时间，平衡性能和实时性

### 注意事项

1. 缓存数据可能存在短暂延迟（最长不超过缓存过期时间）
2. 如需实时数据，可以考虑直接查询数据库或缩短缓存时间
3. Redis 服务不可用时，系统会自动降级到数据库查询

---

## 注意事项

1. 所有时间字段均为 ISO 8601 格式
2. `conversationId` 必须唯一，重复创建会返回 400 错误
3. 删除操作为逻辑删除，不会真正删除数据
4. 分页查询支持按标题和 conversationId 模糊搜索
5. 更新操作只更新传入的非空字段
6. 系统已启用 Redis 缓存，查询性能显著提升
