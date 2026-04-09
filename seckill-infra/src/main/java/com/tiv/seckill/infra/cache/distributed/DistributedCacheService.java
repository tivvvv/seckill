package com.tiv.seckill.infra.cache.distributed;

import java.util.concurrent.TimeUnit;

public interface DistributedCacheService {

    void put(String key, Object value);

    void put(String key, Object value, long timeout);

    void put(String key, Object value, long timeout, TimeUnit unit);

    String getString(String key);

    <T> T getObject(String key, Class<T> targetClass);

    Object getObject(String key);

    Boolean delete(String key);

    Boolean hasKey(String key);

}