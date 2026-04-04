package com.tiv.seckill.domain.repository;

import com.tiv.seckill.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderRepository {

    int saveSeckillOrder(SeckillOrder seckillOrder);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}