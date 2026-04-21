package com.tiv.seckill.order.application.service;

import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

    Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}