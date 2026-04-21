package com.tiv.seckill.goods.domain.service;

import com.tiv.seckill.goods.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsDomainService {

    void saveSeckillGoods(SeckillGoods seckillGoods);

    void updateStatus(Long id, Integer status);

    boolean decreaseAvailableStock(Long id, Integer count);

    boolean decreaseAvailableDbStock(Long id, Integer count);

    SeckillGoods getSeckillGoodsById(Long id);

    List<SeckillGoods> getSeckillGoodsListByActivityId(Long activityId);

    Integer getAvailableStockById(Long id);

}
