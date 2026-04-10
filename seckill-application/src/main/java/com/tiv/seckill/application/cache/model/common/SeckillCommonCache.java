package com.tiv.seckill.application.cache.model.common;

import lombok.Data;

@Data
public class SeckillCommonCache {

    protected boolean exist;

    protected Long version;

    protected boolean retryLater;

}