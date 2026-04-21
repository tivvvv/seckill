package com.tiv.seckill.goods.application.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.cache.local.LocalCacheService;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.lock.DistributedLock;
import com.tiv.seckill.common.lock.DistributedLockFactory;
import com.tiv.seckill.common.util.string.StringUtil;
import com.tiv.seckill.common.util.time.SystemClock;
import com.tiv.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.tiv.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import com.tiv.seckill.goods.domain.model.SeckillGoods;
import com.tiv.seckill.goods.domain.repository.SeckillGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SeckillGoodsListCacheServiceImpl implements SeckillGoodsListCacheService {

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillGoods>>> localCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private SeckillGoodsRepository seckillGoodsRepository;

    private static final String SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY";

    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Override
    public SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version) {
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = localCacheService.getIfPresent(activityId);
        if (seckillGoodsListCache == null) {
            return getDistributedCache(activityId);
        }
        if (version == null || version.compareTo(seckillGoodsListCache.getVersion()) <= 0) {
            log.info("seckillGoodsListCache|命中本地缓存|{}", activityId);
            return seckillGoodsListCache;
        }

        return getDistributedCache(activityId);
    }

    private SeckillBusinessCache<List<SeckillGoods>> getDistributedCache(Long activityId) {
        String cacheKey = buildCacheKey(activityId);
        SeckillBusinessCache<List<SeckillGoods>> SeckillGoodsListCache = SeckillGoodsBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(cacheKey), SeckillGoods.class);
        if (SeckillGoodsListCache == null) {
            // 分布式缓存不存在,尝试更新分布式缓存
            SeckillGoodsListCache = tryUpdateSeckillGoodsListCacheByLock(activityId);
        }
        if (SeckillGoodsListCache != null && !SeckillGoodsListCache.isRetryLater()) {
            // 获取分布式缓存后,尝试加锁更新本地缓存
            if (localCacheUpdateLock.tryLock()) {
                try {
                    localCacheService.put(activityId, SeckillGoodsListCache);
                    log.info("SeckillGoodsListCache|更新本地缓存成功|{}", activityId);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return SeckillGoodsListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsListCacheByLock(Long activityId) {
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId)));
        try {
            // 尝试获取分布式锁
            if (!distributedLock.tryLock(2L, 5L, TimeUnit.SECONDS)) {
                // 获取锁失败,直接返回
                return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
            }
            // 获取分布式锁成功,查询数据库
            List<SeckillGoods> seckillGoodsList = seckillGoodsRepository.getSeckillGoodsListByActivityId(activityId);
            SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache;
            if (CollectionUtils.isEmpty(seckillGoodsList)) {
                seckillGoodsListCache = new SeckillBusinessCache<List<SeckillGoods>>().notExist();
            } else {
                seckillGoodsListCache = new SeckillBusinessCache<List<SeckillGoods>>().with(seckillGoodsList)
                        .withVersion(SystemClock.millisClock().now());
            }
            // 更新分布式缓存
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillGoodsListCache), 5 * 60);
            log.info("seckillGoodsListCache|更新分布式缓存成功|{}", activityId);
            return seckillGoodsListCache;
        } catch (Exception e) {
            log.error("seckillGoodsListCache|更新分布式缓存失败|{}", activityId, e);
            return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
        } finally {
            distributedLock.unLock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(Constants.SECKILL_GOODS_LIST_CACHE_KEY, key);
    }

}