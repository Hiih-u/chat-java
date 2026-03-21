-- 用户表（在 chat-system 管理，这里仅作参考）
CREATE TABLE IF NOT EXISTS sys_user (
                                        id BIGSERIAL PRIMARY KEY,
                                        username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    real_name VARCHAR(50),
    status SMALLINT DEFAULT 1,
    tenant_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
    );

-- 用户令牌表（在 chat-auth 管理）
CREATE TABLE IF NOT EXISTS user_token (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL,
                                          username VARCHAR(50) NOT NULL,
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    access_expire_time TIMESTAMP NOT NULL,
    refresh_expire_time TIMESTAMP,
    login_ip VARCHAR(50),
    user_agent VARCHAR(500),
    device_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
    );

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_token_user_id ON user_token(user_id);
CREATE INDEX IF NOT EXISTS idx_user_token_access_token ON user_token(access_token);
CREATE INDEX IF NOT EXISTS idx_user_token_username ON user_token(username);

-- 插入测试用户（密码为 BCrypt 加密的 "123456"）
INSERT INTO sys_user (username, password, phone, email, real_name, status, tenant_id)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', 'admin@example.com', '管理员', 1, 1)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO sys_user (username, password, phone, email, real_name, status, tenant_id)
VALUES ('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', 'test@example.com', '测试用户', 1, 1)
    ON CONFLICT (username) DO NOTHING;

COMMENT ON TABLE sys_user IS '系统用户表';
COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码（BCrypt加密）';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.status IS '状态：1-正常 0-禁用';
COMMENT ON COLUMN sys_user.tenant_id IS '租户ID';

COMMENT ON TABLE user_token IS '用户令牌表';
COMMENT ON COLUMN user_token.id IS '令牌ID';
COMMENT ON COLUMN user_token.user_id IS '用户ID';
COMMENT ON COLUMN user_token.username IS '用户名';
COMMENT ON COLUMN user_token.access_token IS '访问令牌';
COMMENT ON COLUMN user_token.refresh_token IS '刷新令牌';
COMMENT ON COLUMN user_token.access_expire_time IS '访问令牌过期时间';
COMMENT ON COLUMN user_token.refresh_expire_time IS '刷新令牌过期时间';
COMMENT ON COLUMN user_token.login_ip IS '登录IP';
COMMENT ON COLUMN user_token.user_agent IS '用户代理';
COMMENT ON COLUMN user_token.device_type IS '设备类型';
