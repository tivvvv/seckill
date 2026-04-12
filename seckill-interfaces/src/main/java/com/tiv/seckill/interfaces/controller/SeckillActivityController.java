package com.tiv.seckill.interfaces.controller;

import com.tiv.seckill.application.service.SeckillActivityService;
import com.tiv.seckill.domain.dto.SeckillActivityDTO;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.domain.response.Response;
import com.tiv.seckill.domain.response.ResponseUtils;
import com.tiv.seckill.infra.util.date.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class SeckillActivityController {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @PostMapping("/save")
    public Response<String> saveSeckillActivity(@RequestBody SeckillActivityDTO seckillActivityDTO) {
        seckillActivityService.saveSeckillActivity(seckillActivityDTO);
        return ResponseUtils.success();
    }

    @PostMapping("/update/status")
    public Response<Integer> updateSeckillActivityStatus(@RequestParam(value = "id", required = true) Long id,
                                                         @RequestParam(value = "status", required = true) Integer status) {
        return ResponseUtils.success(seckillActivityService.updateStatus(id, status));
    }

    @GetMapping("/list")
    public Response<List<SeckillActivity>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status) {
        return ResponseUtils.success(seckillActivityService.getSeckillActivityList(status));
    }

    @GetMapping("/list/now")
    public Response<List<SeckillActivity>> getSeckillActivityListByNow(@RequestParam(value = "status", required = false) Integer status,
                                                                       @RequestParam(value = "currentTime", required = true) String currentTime) {
        Date now = DateTimeUtil.parseStringToDate(currentTime, DateTimeUtil.DATE_TIME_FORMAT);
        return ResponseUtils.success(seckillActivityService.getSeckillActivityListByNow(now, status));
    }

    @GetMapping("/info/{id}")
    public Response<SeckillActivity> getSeckillActivityById(@PathVariable Long id) {
        return ResponseUtils.success(seckillActivityService.getSeckillActivityById(id));
    }

    @GetMapping("/list/dto")
    public Response<List<SeckillActivityDTO>> getSeckillActivityList(@RequestParam(value = "status", required = true) Integer status,
                                                                     @RequestParam(value = "version", required = false) Long version) {
        return ResponseUtils.success(seckillActivityService.getSeckillActivityList(status, version));
    }

}