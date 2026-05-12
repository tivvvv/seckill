package com.tiv.seckill.order.domain.service;

import com.tiv.seckill.order.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderDomainService {

    boolean saveSeckillOrder(SeckillOrder seckillOrder);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

    void deleteSeckillOrder(Long orderId);

}