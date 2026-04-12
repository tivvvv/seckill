package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.SeckillActivityDTO;
import com.tiv.seckill.domain.model.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityService {

    void saveSeckillActivity(SeckillActivityDTO seckillActivityDTO);

    int updateStatus(Long id, Integer status);

    List<SeckillActivity> getSeckillActivityList(Integer status);

    List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status);

    SeckillActivity getSeckillActivityById(Long id);

    List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version);

    SeckillActivityDTO getSeckillActivity(Long id, Long version);

}