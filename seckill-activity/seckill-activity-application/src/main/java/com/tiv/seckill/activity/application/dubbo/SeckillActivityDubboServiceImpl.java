package com.tiv.seckill.activity.application.dubbo;

import com.tiv.seckill.activity.application.service.SeckillActivityService;
import com.tiv.seckill.common.model.dto.SeckillActivityDTO;
import com.tiv.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DubboService(version = "1.0.0")
public class SeckillActivityDubboServiceImpl implements SeckillActivityDubboService {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Override
    public SeckillActivityDTO getSeckillActivityDTO(Long id, Long version) {
        return seckillActivityService.getSeckillActivityDTO(id, version);
    }

}