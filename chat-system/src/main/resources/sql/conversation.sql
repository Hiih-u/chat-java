-- 创建表
CREATE TABLE ai_conversations (
                                  id                BIGSERIAL           PRIMARY KEY,
                                  conversation_id   VARCHAR(64)         NOT NULL UNIQUE,
                                  title             VARCHAR(255)        NOT NULL,
                                  session_metadata  JSONB               DEFAULT '{}',
                                  created_at        TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at        TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  deleted           SMALLINT            NOT NULL DEFAULT 0
);

-- 添加注释
COMMENT ON TABLE  ai_conversations                    IS '会话表';
COMMENT ON COLUMN ai_conversations.id                 IS '主键，自增';
COMMENT ON COLUMN ai_conversations.conversation_id    IS '会话唯一标识';
COMMENT ON COLUMN ai_conversations.title              IS '会话标题';
COMMENT ON COLUMN ai_conversations.session_metadata   IS '会话元数据（JSON格式）';
COMMENT ON COLUMN ai_conversations.created_at         IS '创建时间';
COMMENT ON COLUMN ai_conversations.updated_at         IS '更新时间';
COMMENT ON COLUMN ai_conversations.deleted            IS '逻辑删除：0=正常，1=已删除';

-- 创建索引
CREATE INDEX idx_conversation_id ON ai_conversations(conversation_id);
CREATE INDEX idx_deleted          ON ai_conversations(deleted);
CREATE INDEX idx_created_at       ON ai_conversations(created_at);

INSERT INTO ai_conversations
(conversation_id, title, session_metadata, created_at, updated_at, deleted)
VALUES
-- 普通对话
(
    'conv-uuid-1001',
    '如何学习 Spring Cloud 微服务',
    '{"model": "gpt-4", "temperature": 0.7, "language": "zh-CN", "messageCount": 10}',
    '2026-03-01 09:00:00',
    '2026-03-01 09:30:00',
    0
),
-- 代码相关对话
(
    'conv-uuid-1002',
    'PostgreSQL 数据库优化',
    '{"model": "gpt-4", "temperature": 0.5, "language": "zh-CN", "messageCount": 5}',
    '2026-03-05 14:00:00',
    '2026-03-05 14:45:00',
    0
),
-- 空 metadata 对话
(
    'conv-uuid-1003',
    'Java 多线程问题排查',
    '{}',
    '2026-03-10 10:00:00',
    '2026-03-10 10:00:00',
    0
),
-- 已删除的对话（逻辑删除）
(
    'conv-uuid-1004',
    '已删除的测试会话',
    '{"model": "gpt-3.5", "temperature": 1.0, "language": "en-US", "messageCount": 2}',
    '2026-03-12 08:00:00',
    '2026-03-12 08:10:00',
    1
),
-- 最新对话
(
    'conv-uuid-1005',
    'MyBatis-Plus 使用指南',
    '{"model": "gpt-4", "temperature": 0.8, "language": "zh-CN", "messageCount": 20, "tags": ["java", "mybatis"]}',
    '2026-03-19 09:00:00',
    '2026-03-19 11:00:00',
    0
);