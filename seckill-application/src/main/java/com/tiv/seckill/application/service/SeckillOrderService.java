package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.SeckillOrderDTO;
import com.tiv.seckill.domain.model.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

    SeckillOrder saveSeckillOrder(SeckillOrderDTO seckillOrderDTO);

    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}