package com.ai.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * 网关白名单配置
 * <p>
 * 不加 @Component，由 @EnableConfigurationProperties 在 GatewayApplication 上注册。
 * 这样 @RefreshScope 能正确包裹唯一的绑定实例，保证 Nacos 修改白名单后热更新生效。
 * </p>
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private List<String> whiteList;
}
