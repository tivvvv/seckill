package com.tiv.seckill.application.service;

import com.tiv.seckill.application.command.SeckillOrderCommand;
import com.tiv.seckill.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

    Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}