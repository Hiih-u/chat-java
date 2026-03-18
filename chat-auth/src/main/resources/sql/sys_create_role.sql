-- 1. 用户表
CREATE TABLE sys_user (
                          id BIGINT NOT NULL,
                          username VARCHAR(64) NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          nickname VARCHAR(64),
                          phone VARCHAR(20),
                          status SMALLINT DEFAULT 1 NOT NULL, -- 状态: 0-禁用, 1-启用
                          is_deleted SMALLINT DEFAULT 0 NOT NULL, -- 逻辑删除: 0-未删除, 1-已删除
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id)
);
COMMENT ON TABLE sys_user IS '系统用户表';
COMMENT ON COLUMN sys_user.username IS '登录账号';
COMMENT ON COLUMN sys_user.password IS '密码(BCrypt加密)';
COMMENT ON COLUMN sys_user.status IS '账号状态(0停用 1正常)';

-- 2. 角色表
CREATE TABLE sys_role (
                          id BIGINT NOT NULL,
                          role_name VARCHAR(64) NOT NULL,
                          role_code VARCHAR(64) NOT NULL, -- 角色权限字符串，如 admin, common_user
                          description VARCHAR(255),
                          status SMALLINT DEFAULT 1 NOT NULL,
                          is_deleted SMALLINT DEFAULT 0 NOT NULL,
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id)
);
COMMENT ON TABLE sys_role IS '系统角色表';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';

-- 3. 菜单/权限表
CREATE TABLE sys_menu (
                          id BIGINT NOT NULL,
                          parent_id BIGINT DEFAULT 0 NOT NULL,
                          menu_name VARCHAR(64) NOT NULL,
                          path VARCHAR(255), -- 路由地址
                          component VARCHAR(255), -- 组件路径
                          perms VARCHAR(100), -- 权限标识，如 system:user:add
                          icon VARCHAR(100), -- 菜单图标
                          menu_type SMALLINT NOT NULL, -- 菜单类型: 0-目录, 1-菜单, 2-按钮
                          sort_order INT DEFAULT 0, -- 显示顺序
                          is_deleted SMALLINT DEFAULT 0 NOT NULL,
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id)
);
COMMENT ON TABLE sys_menu IS '系统菜单权限表';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型(0目录 1菜单 2按钮)';

-- 4. 用户-角色关联表
CREATE TABLE sys_user_role (
                               id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               role_id BIGINT NOT NULL,
                               PRIMARY KEY (id)
);
COMMENT ON TABLE sys_user_role IS '用户和角色关联表';
-- 建议在业务稳定后加上联合唯一索引
-- CREATE UNIQUE INDEX uk_user_role ON sys_user_role(user_id, role_id);

-- 5. 角色-菜单关联表
CREATE TABLE sys_role_menu (
                               id BIGINT NOT NULL,
                               role_id BIGINT NOT NULL,
                               menu_id BIGINT NOT NULL,
                               PRIMARY KEY (id)
);
COMMENT ON TABLE sys_role_menu IS '角色和菜单关联表';
-- CREATE UNIQUE INDEX uk_role_menu ON sys_role_menu(role_id, menu_id);