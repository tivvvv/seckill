package com.tiv.seckill.goods.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiv.seckill.goods.domain.model.SeckillGoods;
import org.apache.ibatis.annotations.Param;

public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    int decreaseAvailableStock(@Param("id") Long id, @Param("count") Integer count);

}