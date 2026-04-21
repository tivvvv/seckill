package com.tiv.seckill.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCodeEnum ErrorCodeEnum) {
        super(ErrorCodeEnum.getMessage());
        this.code = ErrorCodeEnum.getCode();
    }

    public BusinessException(ErrorCodeEnum ErrorCodeEnum, String message) {
        super(message);
        this.code = ErrorCodeEnum.getCode();
    }

}
