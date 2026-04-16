package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    void saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO);

    void updateStatus(Long id, Integer status);

    void decreaseAvailableStock(Long id, Integer count);

    SeckillGoods getSeckillGoodsById(Long id);

    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    Integer getAvailableStockById(Long id);

    List<SeckillGoodsDTO> getSeckillGoodsDTOList(Long activityId, Long version);

    SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version);

}