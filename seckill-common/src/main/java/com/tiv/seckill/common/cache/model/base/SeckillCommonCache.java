package com.tiv.seckill.common.cache.model.base;

import lombok.Data;

@Data
public class SeckillCommonCache {

    protected boolean exist;

    protected Long version;

    protected boolean retryLater;

}