package com.tiv.seckill.goods.application.service;

import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.goods.application.command.SeckillGoodsCommand;
import com.tiv.seckill.goods.domain.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand);

    void updateStatus(Long id, Integer status);

    boolean decreaseAvailableStock(Long id, Integer count);

    boolean decreaseAvailableDbStock(Long id, Integer count);

    boolean increaseAvailableStock(Long id, Integer count);

    SeckillGoods getSeckillGoodsById(Long id);

    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    Integer getAvailableStockById(Long id);

    List<SeckillGoodsDTO> getSeckillGoodsDTOList(Long activityId, Long version);

    SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version);

}