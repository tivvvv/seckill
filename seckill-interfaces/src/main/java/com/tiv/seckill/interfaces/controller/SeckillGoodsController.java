package com.tiv.seckill.interfaces.controller;

import com.tiv.seckill.application.command.SeckillGoodsCommand;
import com.tiv.seckill.application.service.SeckillGoodsService;
import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.model.SeckillGoods;
import com.tiv.seckill.domain.response.Response;
import com.tiv.seckill.domain.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @PostMapping("/save")
    public Response<String> saveSeckillGoods(@RequestBody SeckillGoodsCommand seckillGoodsCommand) {
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommand);
        return ResponseUtils.success();
    }

    @PostMapping("/update/status")
    public Response<String> updateSeckillGoodsStatus(@RequestParam(value = "id", required = true) Long id,
                                                     @RequestParam(value = "status", required = true) Integer status) {
        seckillGoodsService.updateStatus(id, status);
        return ResponseUtils.success();
    }

    @GetMapping("/info/{id}")
    public Response<SeckillGoods> getSeckillGoodsById(@PathVariable Long id) {
        return ResponseUtils.success(seckillGoodsService.getSeckillGoodsById(id));
    }

    @GetMapping("/list/activity/{activityId}")
    public Response<List<SeckillGoods>> getSeckillGoodsList(@PathVariable Long activityId) {
        return ResponseUtils.success(seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }

    @GetMapping("/list/dto")
    public Response<List<SeckillGoodsDTO>> getSeckillGoodsDTOList(@RequestParam(value = "activityId", required = true) Long activityId,
                                                                  @RequestParam(value = "version", required = false) Long version) {
        return ResponseUtils.success(seckillGoodsService.getSeckillGoodsDTOList(activityId, version));
    }

    @GetMapping("/dto")
    public Response<SeckillGoodsDTO> getSeckillGoodsDTO(@RequestParam(value = "id", required = true) Long id,
                                                        @RequestParam(value = "version", required = false) Long version) {
        return ResponseUtils.success(seckillGoodsService.getSeckillGoodsDTO(id, version));
    }

}