package com.tiv.seckill.infra.lock;

public interface DistributedLockFactory {

    DistributedLock getDistributedLock(String key);

}