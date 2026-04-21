package com.tiv.seckill.common.lock;

public interface DistributedLockFactory {

    DistributedLock getDistributedLock(String key);

}