package com.ai.chat.controller;

import com.ai.chat.config.AuthProperties;
import com.ai.chat.config.JwtProperties;
import com.ai.chat.config.NacosConfigTestProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nacos Config 动态刷新测试接口
 * <p>
 * 测试步骤：
 * 1. 启动网关，访问 GET /gateway/config/current 查看当前配置值
 * 2. 登录 Nacos 控制台，修改 chat-gateway.yaml 中的 app.config.* 或 auth.white-list
 * 3. 保存后无需重启，再次访问该接口，观察配置是否已动态刷新
 * </p>
 */
@RestController
@RequestMapping("/gateway/config")
@RequiredArgsConstructor
public class NacosConfigController {

    /** 动态刷新测试属性（@RefreshScope，热更新） */
    private final NacosConfigTestProperties testProperties;

    /** JWT 配置（@RefreshScope，热更新） */
    private final JwtProperties jwtProperties;

    /** 白名单配置（@RefreshScope，热更新） */
    private final AuthProperties authProperties;

    /**
     * 查看当前生效的 Nacos 配置
     *
     * <p>修改 Nacos 中 chat-gateway.yaml 配置后，无需重启，
     * 再次调用此接口即可看到最新值，证明动态刷新生效。</p>
     *
     * @return 当前配置快照
     */
    @GetMapping("/current")
    public Mono<Map<String, Object>> currentConfig() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("queriedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("message", "以下值来自 Nacos，修改 chat-gateway.yaml 后无需重启即可刷新");

        // app.config.* 测试属性
        Map<String, Object> appConfig = new LinkedHashMap<>();
        appConfig.put("testValue", testProperties.getTestValue());
        appConfig.put("description", testProperties.getDescription());
        appConfig.put("featureEnabled", testProperties.isFeatureEnabled());
        appConfig.put("rateLimit", testProperties.getRateLimit());
        result.put("appConfig", appConfig);

        // auth.white-list 白名单
        result.put("authWhiteList", authProperties.getWhiteList());

        // jwt.secret 脱敏展示（仅显示前 6 位 + ***）
        String secret = jwtProperties.getSecret();
        String maskedSecret = (secret != null && secret.length() > 6)
                ? secret.substring(0, 6) + "***"
                : "(未配置)";
        result.put("jwtSecretPreview", maskedSecret);

        return Mono.just(result);
    }

    /**
     * 仅查看 app.config.* 测试属性（最小化验证动态刷新）
     *
     * <p>在 Nacos 中修改 app.config.test-value 等字段后，
     * 调用此接口即可实时看到最新值。</p>
     *
     * @return app.config 测试属性
     */
    @GetMapping("/test-props")
    public Mono<Map<String, Object>> testProps() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("testValue", testProperties.getTestValue());
        result.put("description", testProperties.getDescription());
        result.put("featureEnabled", testProperties.isFeatureEnabled());
        result.put("rateLimit", testProperties.getRateLimit());
        result.put("tip", "修改 Nacos chat-gateway.yaml 中 app.config.* 后，此值将实时更新");
        return Mono.just(result);
    }
}
