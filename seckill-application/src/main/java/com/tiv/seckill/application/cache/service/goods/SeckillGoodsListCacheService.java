package com.tiv.seckill.application.cache.service.goods;

import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.common.SeckillCacheService;
import com.tiv.seckill.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsListCacheService extends SeckillCacheService {

    SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version);

    SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsListCacheByLock(Long activityId);

}