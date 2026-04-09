package com.tiv.seckill.infra.cache.distributed.redis;

import com.alibaba.fastjson.JSON;
import com.tiv.seckill.infra.cache.distributed.DistributedCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的分布式缓存实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "cache.distributed.type", havingValue = "redis")
public class RedisCacheService implements DistributedCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(String key, Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, Object value, long timeout) {
        this.put(key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public String getString(String key) {
        Object value = this.getObject(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    @Override
    public <T> T getObject(String key, Class<T> targetClass) {
        Object value = this.getObject(key);
        if (value == null) {
            return null;
        }
        try {
            return JSON.parseObject((String) value, targetClass);
        } catch (Exception e) {
            log.error("Failed to parse object from redis cache, key: {}, value: {}", key, value, e);
            return null;
        }
    }

    @Override
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean delete(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

}