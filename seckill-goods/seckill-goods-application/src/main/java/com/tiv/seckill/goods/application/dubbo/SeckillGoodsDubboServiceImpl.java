package com.tiv.seckill.goods.application.dubbo;

import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.tiv.seckill.goods.application.service.SeckillGoodsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Override
    public SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version) {
        return seckillGoodsService.getSeckillGoodsDTO(id, version);
    }

    @Override
    public boolean decreaseAvailableDbStock(Long id, Integer count) {
        return seckillGoodsService.decreaseAvailableDbStock(id, count);
    }

}