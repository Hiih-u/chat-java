package com.ai.chat.system.api.entity;

import lombok.Data;

/**
 * 用户信息（内部服务调用共享实体）
 * 仅用于 chat-auth 等内部服务通过 Feign 获取用户信息（含密码），不对外暴露
 */
@Data
public class UserInfo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt 加密，内部接口专用）
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 状态：1-正常 0-禁用
     */
    private Integer status;

    /**
     * 租户ID
     */
    private Long tenantId;
}
