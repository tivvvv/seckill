package com.tiv.seckill.application.cache.service.activity;

import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.common.SeckillCacheService;
import com.tiv.seckill.domain.model.SeckillActivity;

import java.util.List;

public interface SeckillActivityListCacheService extends SeckillCacheService {

    SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version);

    SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivitiesCacheByLock(Integer status);

}