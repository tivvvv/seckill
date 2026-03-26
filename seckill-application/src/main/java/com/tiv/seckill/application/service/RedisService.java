package com.tiv.seckill.application.service;

public interface RedisService {

    /**
     * 设置缓存
     */
    void set(String key, Object value);

}