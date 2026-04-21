package com.tiv.seckill.dubbo.interfaces.activity;

import com.tiv.seckill.common.model.dto.SeckillActivityDTO;

public interface SeckillActivityDubboService {

    SeckillActivityDTO getSeckillActivityDTO(Long id, Long version);

}