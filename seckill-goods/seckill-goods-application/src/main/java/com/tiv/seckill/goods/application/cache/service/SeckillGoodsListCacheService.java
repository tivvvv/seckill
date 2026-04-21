package com.tiv.seckill.goods.application.cache.service;

import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.cache.service.SeckillCacheService;
import com.tiv.seckill.goods.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsListCacheService extends SeckillCacheService {

    SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version);

    SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsListCacheByLock(Long activityId);

}