package com.tiv.seckill.infra.handler.exception;

import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.response.Response;
import com.tiv.seckill.domain.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Response<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResponseUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ResponseUtils.error(ErrorCodeEnum.PARAMS_ERROR, errorMsg);
    }

    /**
     * 运行时异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public Response<?> businessExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResponseUtils.error(ErrorCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Response<?> businessExceptionHandler(Exception e) {
        log.error("Exception", e);
        return ResponseUtils.error(ErrorCodeEnum.SYSTEM_ERROR);
    }

}