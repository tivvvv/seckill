package com.tiv.seckill.dubbo.interfaces.goods;

import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import org.dromara.hmily.annotation.Hmily;

public interface SeckillGoodsDubboService {

    SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version);

    boolean decreaseAvailableDbStock(Long id, Integer count);

    @Hmily
    boolean decreaseAvailableStock(Long id, Integer count, Long txId);

}