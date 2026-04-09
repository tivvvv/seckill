package com.tiv.seckill.infra.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    boolean tryLock(Long waitTime, Long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    void lock(Long leaseTime, TimeUnit timeUnit);

    void unLock();

    boolean isLocked();

    boolean isHeldByCurrentThread();

    boolean isHeldByThread(Long threadId);

}