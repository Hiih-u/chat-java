package com.ai.chat;

import com.ai.chat.config.AuthProperties;
import com.ai.chat.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关启动类
 *
 * <p>使用 @EnableConfigurationProperties 统一注册配置属性类，
 * 而不是在各配置类上加 @Component。
 * 这样 Spring Cloud 能正确地将 @RefreshScope 包裹在唯一的绑定实例上，
 * 保证 Nacos 配置变更后 @RefreshScope Bean 热更新正常生效。</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({
        JwtProperties.class,
        AuthProperties.class
})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
