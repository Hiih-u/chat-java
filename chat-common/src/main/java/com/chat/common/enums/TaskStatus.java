package com.chat.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TaskStatus {
    PENDING(0, "待处理"),
    SUCCESS(1, "成功"),
    FAILED(2, "失败"),
    PROCESSING(3, "处理中");

    @JsonValue
    @EnumValue
    private final int value;
    private final String description;

    TaskStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }
}
