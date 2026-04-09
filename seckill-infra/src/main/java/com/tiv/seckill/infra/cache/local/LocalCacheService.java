package com.tiv.seckill.infra.cache.local;

public interface LocalCacheService<K, V> {

    void put(K key, V value);

    V getIfPresent(K key);

}