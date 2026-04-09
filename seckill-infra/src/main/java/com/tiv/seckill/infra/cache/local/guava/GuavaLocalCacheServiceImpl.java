package com.tiv.seckill.infra.cache.local.guava;

import com.google.common.cache.Cache;
import com.tiv.seckill.infra.cache.local.LocalCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 基于Guava的本地缓存实现
 *
 * @param <K>
 * @param <V>
 */
@Service
@ConditionalOnProperty(name = "local.cache.type", havingValue = "guava")
public class GuavaLocalCacheServiceImpl<K, V> implements LocalCacheService<K, V> {

    private final Cache<K, V> cache = GuavaLocalCacheFactory.getLocalCache();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V getIfPresent(K key) {
        return cache.getIfPresent(key);
    }

}