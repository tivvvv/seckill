package com.tiv.seckill.activity.application.cache.service.impl;

import com.alibaba.fastjson2.JSON;
import com.tiv.seckill.activity.application.builder.SeckillActivityBuilder;
import com.tiv.seckill.activity.application.cache.service.SeckillActivityCacheService;
import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.activity.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.cache.local.LocalCacheService;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.lock.DistributedLock;
import com.tiv.seckill.common.lock.DistributedLockFactory;
import com.tiv.seckill.common.util.string.StringUtil;
import com.tiv.seckill.common.util.time.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SeckillActivityCacheServiceImpl implements SeckillActivityCacheService {

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<SeckillActivity>> localCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    private static final String SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY";

    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Override
    public SeckillBusinessCache<SeckillActivity> getCachedActivity(Long activityId, Long version) {
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = localCacheService.getIfPresent(activityId);
        if (seckillActivityCache == null) {
            return getDistributedCache(activityId);
        }
        if (version == null || version.compareTo(seckillActivityCache.getVersion()) <= 0) {
            log.info("seckillActivityCache|命中本地缓存|{}", activityId);
            return seckillActivityCache;
        } else {
            return getDistributedCache(activityId);
        }
    }

    private SeckillBusinessCache<SeckillActivity> getDistributedCache(Long activityId) {
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = SeckillActivityBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(activityId)), SeckillActivity.class);
        if (seckillActivityCache == null) {
            seckillActivityCache = tryUpdateSeckillActivityCacheByLock(activityId);
        }
        if (seckillActivityCache != null && !seckillActivityCache.isRetryLater()) {
            if (localCacheUpdateLock.tryLock()) {
                try {
                    localCacheService.put(activityId, seckillActivityCache);
                    log.info("seckillActivityCache|更新本地缓存成功|{}", activityId);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillActivityCache;
    }

    @Override
    public SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId) {
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId)));
        try {
            if (!distributedLock.tryLock(1L, 5L, TimeUnit.SECONDS)) {
                return new SeckillBusinessCache<SeckillActivity>().retryLater();
            }
            SeckillActivity seckillActivity = seckillActivityRepository.getSeckillActivityById(activityId);
            SeckillBusinessCache<SeckillActivity> seckillActivityCache;
            if (seckillActivity == null) {
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>().notExist();
            } else {
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>()
                        .with(seckillActivity)
                        .withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillActivityCache), 5 * 60);
            log.info("seckillActivityCache|更新分布式缓存成功|{}", activityId);
            return seckillActivityCache;
        } catch (Exception e) {
            log.error("seckillActivityCache|更新分布式缓存失败|{}", activityId, e);
            return new SeckillBusinessCache<SeckillActivity>().retryLater();
        } finally {
            distributedLock.unLock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(Constants.SECKILL_ACTIVITY_CACHE_KEY, key);
    }

}