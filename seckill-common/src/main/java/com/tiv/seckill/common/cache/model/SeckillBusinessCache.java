package com.tiv.seckill.common.cache.model;

import com.tiv.seckill.common.cache.model.base.SeckillCommonCache;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillBusinessCache<T> extends SeckillCommonCache {

    private T data;

    public SeckillBusinessCache<T> with(T data) {
        this.data = data;
        this.exist = true;
        return this;
    }

    public SeckillBusinessCache<T> withVersion(Long version) {
        this.version = version;
        return this;
    }

    public SeckillBusinessCache<T> retryLater() {
        this.retryLater = true;
        return this;
    }

    public SeckillBusinessCache<T> notExist() {
        this.exist = false;
        return this;
    }

}