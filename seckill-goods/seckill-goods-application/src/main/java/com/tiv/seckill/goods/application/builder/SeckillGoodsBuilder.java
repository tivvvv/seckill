package com.tiv.seckill.goods.application.builder;

import com.tiv.seckill.common.builder.SeckillCommonBuilder;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.common.util.bean.BeanUtil;
import com.tiv.seckill.goods.application.command.SeckillGoodsCommand;
import com.tiv.seckill.goods.domain.model.SeckillGoods;

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