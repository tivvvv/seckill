package com.tiv.seckill.activity.application.cache.service;

import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.cache.service.SeckillCacheService;

public interface SeckillActivityCacheService extends SeckillCacheService {

    SeckillBusinessCache<SeckillActivity> getCachedActivity(Long activityId, Long version);

    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId);

}