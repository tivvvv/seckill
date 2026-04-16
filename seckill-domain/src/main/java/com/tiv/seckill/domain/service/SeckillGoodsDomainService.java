package com.tiv.seckill.domain.service;

import com.tiv.seckill.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsDomainService {

    void saveSeckillGoods(SeckillGoods seckillGoods);

    void updateStatus(Long id, Integer status);

    void decreaseAvailableStock(Long id, Integer count);

    SeckillGoods getSeckillGoodsById(Long id);

    List<SeckillGoods> getSeckillGoodsListByActivityId(Long activityId);

    Integer getAvailableStockById(Long id);

}
