package com.ai.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "auth") // 读取配置文件中以 auth 为前缀的配置项，自动映射到类的字段上。
public class AuthProperties {
    private List<String> whiteList;
}
