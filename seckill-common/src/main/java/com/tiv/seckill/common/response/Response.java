package com.tiv.seckill.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应封装类
 */
@Data
@AllArgsConstructor
public class Response<T> implements Serializable {

    private int code;

    private T data;

}