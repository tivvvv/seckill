package com.tiv.seckill.infra.lock.redisson;

import com.tiv.seckill.infra.lock.DistributedLock;
import com.tiv.seckill.infra.lock.DistributedLockFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的分布式锁工厂
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "lock.distributed.type", havingValue = "redisson")
public class RedissonLockFactory implements DistributedLockFactory {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public DistributedLock getDistributedLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        return new DistributedLock() {
            @Override
            public boolean tryLock(Long waitTime, Long leaseTime, TimeUnit timeUnit) throws InterruptedException {
                boolean isLockSuccess = rLock.tryLock(waitTime, leaseTime, timeUnit);
                log.info("RedissonLockFactory--getDistributedLock--tryLock key: {} try lock result: {}", key, isLockSuccess);
                return isLockSuccess;
            }

            @Override
            public void lock(Long leaseTime, TimeUnit timeUnit) {
                rLock.lock(leaseTime, timeUnit);
            }

            @Override
            public void unLock() {
                if (this.isLocked() && this.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            }

            @Override
            public boolean isLocked() {
                return rLock.isLocked();
            }

            @Override
            public boolean isHeldByCurrentThread() {
                return rLock.isHeldByCurrentThread();
            }

            @Override
            public boolean isHeldByThread(Long threadId) {
                return rLock.isHeldByThread(threadId);
            }
        };
    }

}