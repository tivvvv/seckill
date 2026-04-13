package com.tiv.seckill.domain.repository;

import com.tiv.seckill.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsRepository {

    int saveSeckillGoods(SeckillGoods seckillGoods);

    int updateStatus(Long id, Integer status);

    int decreaseAvailableStock(Long id, Integer count);

    SeckillGoods getSeckillGoodsById(Long id);

    List<SeckillGoods> getSeckillGoodsListByActivityId(Long activityId);

    Integer getAvailableStockById(Long id);

}