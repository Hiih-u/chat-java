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

## 注意事项

1. 所有时间字段均为 ISO 8601 格式
2. `conversationId` 必须唯一，重复创建会返回 400 错误
3. 删除操作为逻辑删除，不会真正删除数据
4. 分页查询支持按标题和 conversationId 模糊搜索
5. 更新操作只更新传入的非空字段
