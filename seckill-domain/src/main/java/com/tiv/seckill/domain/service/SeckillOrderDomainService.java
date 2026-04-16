package com.tiv.seckill.domain.service;

import com.tiv.seckill.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderDomainService {

    boolean saveSeckillOrder(SeckillOrder seckillOrder);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}