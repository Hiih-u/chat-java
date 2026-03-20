package com.ai.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Nacos Config 动态刷新测试配置
 * <p>
 * 对应 Nacos 中 chat-gateway.yaml 的 app.config 前缀。
 * </p>
 *
 * <p><b>注意：不加 @Component。</b>
 * 在 Spring Boot 3.x 中，@ConfigurationProperties + @RefreshScope + @Component 三者共用，
 * 会导致 Spring 同时创建「原始 Bean」和「RefreshScope 代理 Bean」，
 * ConfigurationPropertiesBindingPostProcessor 将属性绑定到原始 Bean，
 * 而容器中实际注入的是代理 Bean，刷新时代理 Bean 重建后属性为空/默认值。
 * 改为由 @EnableConfigurationProperties 在 GatewayApplication 上统一注册，
 * Spring Cloud 会正确地将 @RefreshScope 包裹在唯一的绑定实例上。
 * </p>
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "app.config")
public class NacosConfigTestProperties {

    /** 测试用字符串，可在 Nacos 随意修改观察热更新效果 */
    private String testValue = "default-value";

    /** 应用描述信息 */
    private String description = "Nacos Config 动态刷新测试";

    /** 功能开关，true=开启，false=关闭 */
    private boolean featureEnabled = false;

    /** 限流阈值（示例数值配置） */
    private int rateLimit = 100;
}
