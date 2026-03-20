-- 会话表结构 (兼容 PostgreSQL 语法)
DROP TABLE IF EXISTS ai_conversations;

CREATE TABLE ai_conversations (
    -- 使用 BIGSERIAL 替代 BIGINT AUTO_INCREMENT (PostgreSQL 的自增语法)
                                  id BIGSERIAL PRIMARY KEY,
                                  conversation_id VARCHAR(128) NOT NULL,
                                  title VARCHAR(255),
    -- H2 中用 VARCHAR 替代 PG 的 JSONB
                                  session_metadata VARCHAR(2000),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  deleted INT DEFAULT 0
);

-- 唯一索引
CREATE UNIQUE INDEX uk_conversation_id ON ai_conversations(conversation_id);