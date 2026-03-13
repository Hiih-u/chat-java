package com.ai.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Map<String,Object> health() {
        Map<String,Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "chat-gateway");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
}
