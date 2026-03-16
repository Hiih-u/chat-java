package com.ai.chat.common.pojo.entity;

import com.ai.chat.common.enums.ResultCode;
import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> error(String message) {
        return build(ResultCode.INTERNAL_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return build(resultCode, null);
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return build(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return build(code, message, null);
    }

    public static <T> Result<T> build(ResultCode resultCode, T data) {
        return build(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> Result<T> build(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
