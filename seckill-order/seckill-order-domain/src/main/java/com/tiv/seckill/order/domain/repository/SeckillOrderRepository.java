package com.tiv.seckill.order.domain.repository;

import com.tiv.seckill.order.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderRepository {

    boolean saveSeckillOrder(SeckillOrder seckillOrder);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

    boolean deleteSeckillOrder(Long orderId);

}