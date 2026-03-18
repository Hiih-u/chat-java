package com.ai.chat.auth.pojo.dto;

import lombok.Data;

/**
 * 用户信息 DTO（从 chat-system 获取）
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
     * 密码
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
