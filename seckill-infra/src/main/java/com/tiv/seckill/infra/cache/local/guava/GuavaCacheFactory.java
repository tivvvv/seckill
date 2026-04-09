package com.tiv.seckill.infra.cache.local.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class GuavaCacheFactory {

    public static <K, V> Cache<K, V> getLocalCache() {
        return CacheBuilder.newBuilder()
                .initialCapacity(15)
                .concurrencyLevel(5)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();
    }

}