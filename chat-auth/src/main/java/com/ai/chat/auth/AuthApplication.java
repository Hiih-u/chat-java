package com.ai.chat.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证服务启动类
 */
@SpringBootApplication(scanBasePackages = {
        "com.ai.chat.auth",
        "com.ai.chat.common"
})
@MapperScan("com.ai.chat.auth.mapper")
@EnableFeignClients(basePackages = "com.ai.chat.auth.client")
@EnableDiscoveryClient
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("""
            
            ========================================
            🚀 Chat-Auth 认证服务启动成功！
            ========================================
            📝 登录接口: POST /auth/login
            🚪 登出接口: POST /auth/logout
            🔄 刷新令牌: POST /auth/refresh
            ✅ 验证令牌: GET /auth/validate
            ❤️  健康检查: GET /auth/health
            ========================================
            
            """);
    }
}
