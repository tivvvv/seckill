package com.tiv.seckill.application.builder;

import com.tiv.seckill.application.builder.common.SeckillCommonBuilder;
import com.tiv.seckill.application.command.SeckillGoodsCommand;
import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.model.SeckillGoods;
import com.tiv.seckill.infra.util.bean.BeanUtil;

public class SeckillGoodsBuilder extends SeckillCommonBuilder {

    public static SeckillGoods toSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        if (seckillGoodsCommand == null) {
            return null;
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsCommand, seckillGoods);
        return seckillGoods;
    }

    public static SeckillGoodsDTO toSeckillGoodsDTO(SeckillGoods seckillGoods) {
        if (seckillGoods == null) {
            return null;
        }
        SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
        BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
        return seckillGoodsDTO;
    }

}