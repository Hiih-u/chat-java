package com.ai.chat.common.handler;


import com.ai.chat.common.entity.Result;
import com.ai.chat.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    public Result<Void> handleBusinessException(BusinessException exception) {
        log.warn("业务异常: code={},message={}",exception.getCode(),exception.getMessage());
        return Result.error(exception.getCode(), exception.getMessage());
    }

}
