package com.tiv.seckill.common.cache.distributed;

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

    Long addSet(String key, Object... values);

    Long removeSet(String key, Object... values);

    Boolean inSet(String key, Object value);

    Long increment(String key, Long delta);

    Long decrement(String key, Long delta);

    Long initByLua(String key, Integer quantity);

    Long incrementByLua(String key, Integer quantity);

    Long decrementByLua(String key, Integer quantity);

    void checkLuaResult(Long result);

}