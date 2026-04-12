package com.tiv.seckill.application.cache.service.activity;

import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.common.SeckillCacheService;
import com.tiv.seckill.domain.model.SeckillActivity;

public interface SeckillActivityCacheService extends SeckillCacheService {

    SeckillBusinessCache<SeckillActivity> getCachedActivity(Long activityId, Long version);

    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId);

}