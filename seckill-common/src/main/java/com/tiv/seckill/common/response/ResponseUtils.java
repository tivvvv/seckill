package com.tiv.seckill.common.response;

import com.tiv.seckill.common.exception.ErrorCodeEnum;

/**
 * 响应工具类
 */
public class ResponseUtils {

    /**
     * 成功
     *
     * @param <T>
     * @return
     */
    public static <T> Response<T> success() {
        return new Response<>(ErrorCodeEnum.SUCCESS.getCode(), null);
    }

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(ErrorCodeEnum.SUCCESS.getCode(), data);
    }

    /**
     * 失败
     *
     * @param errorCodeEnum
     * @return
     */
    public static Response<?> error(ErrorCodeEnum errorCodeEnum) {
        return new Response<>(errorCodeEnum.getCode(), errorCodeEnum.getMessage());
    }

    /**
     * 失败
     *
     * @param errorCodeEnum
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> error(ErrorCodeEnum errorCodeEnum, T data) {
        return new Response<>(errorCodeEnum.getCode(), data);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static Response<?> error(int code, String message) {
        return new Response<>(code, message);
    }

}