package com.tiv.seckill.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiv.seckill.domain.model.SeckillGoods;
import org.apache.ibatis.annotations.Param;

public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    int decreaseAvailableStock(@Param("id") Long id, @Param("count") Integer count);

}