package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    int saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO);

    int updateStatus(Long id, Integer status);

    int decreaseAvailableStock(Long id, Integer count);

    SeckillGoods getSeckillGoodsById(Long id);

    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    Integer getAvailableStockById(Long id);

    List<SeckillGoodsDTO> getSeckillGoodsDTOList(Long activityId, Long version);

}