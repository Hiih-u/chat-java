package com.ai.chat.auth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户令牌实体
 */
@Data
@TableName("user_token")
public class UserToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌过期时间
     */
    private LocalDateTime accessExpireTime;

    /**
     * 刷新令牌过期时间
     */
    private LocalDateTime refreshExpireTime;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
