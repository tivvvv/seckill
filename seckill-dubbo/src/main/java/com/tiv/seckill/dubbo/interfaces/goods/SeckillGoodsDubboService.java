package com.tiv.seckill.dubbo.interfaces.goods;

import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;

public interface SeckillGoodsDubboService {

    SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version);

    boolean decreaseAvailableDbStock(Long id, Integer count);

}