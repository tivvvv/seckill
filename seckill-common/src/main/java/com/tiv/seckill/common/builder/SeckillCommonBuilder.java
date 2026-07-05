
package com.tiv.seckill.common.builder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;

import java.util.List;

public class SeckillCommonBuilder {

    public static <T> SeckillBusinessCache<T> getSeckillBusinessCache(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<T>>(clazz) {
        });
    }

    public static <T> SeckillBusinessCache<List<T>> getSeckillBusinessCacheList(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<List<T>>>(clazz) {
        });
    }

}