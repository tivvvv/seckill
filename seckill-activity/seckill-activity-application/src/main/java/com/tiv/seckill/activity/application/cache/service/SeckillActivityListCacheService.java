package com.tiv.seckill.activity.application.cache.service;

import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.cache.service.SeckillCacheService;

import java.util.List;

public interface SeckillActivityListCacheService extends SeckillCacheService {

    SeckillBusinessCache<List<SeckillActivity>> getCachedActivityList(Integer status, Long version);

    SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityListCacheByLock(Integer status);

}