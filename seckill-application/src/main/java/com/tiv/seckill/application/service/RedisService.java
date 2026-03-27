package com.tiv.seckill.application.service;

public interface RedisService {

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    void set(String key, Object value);

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    Object get(String key);

}