package com.tiv.seckill.activity.application.service;

import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.common.model.dto.SeckillActivityDTO;

import java.util.Date;
import java.util.List;

public interface SeckillActivityService {

    void saveSeckillActivity(SeckillActivityDTO seckillActivityDTO);

    void updateStatus(Long id, Integer status);

    List<SeckillActivity> getSeckillActivityDTOList(Integer status);

    List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status);

    SeckillActivity getSeckillActivityById(Long id);

    List<SeckillActivityDTO> getSeckillActivityDTOList(Integer status, Long version);

    SeckillActivityDTO getSeckillActivityDTO(Long id, Long version);

}