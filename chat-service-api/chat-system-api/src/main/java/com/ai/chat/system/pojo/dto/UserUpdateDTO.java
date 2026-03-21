package com.ai.chat.system.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新 DTO
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Schema(description = "用户名")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "状态：1-正常 0-禁用")
    private Integer status;

    @Schema(description = "租户ID")
    private Long tenantId;
}
