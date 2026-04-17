package com.tiv.seckill.interfaces.controller;

import com.tiv.seckill.application.command.SeckillOrderCommand;
import com.tiv.seckill.application.service.SeckillOrderService;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.domain.response.Response;
import com.tiv.seckill.domain.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @PostMapping("/save")
    public Response<Long> saveSeckillOrder(@RequestAttribute Long userId, @RequestBody SeckillOrderCommand seckillOrderCommand) {
        return ResponseUtils.success(seckillOrderService.saveSeckillOrder(userId, seckillOrderCommand));
    }

    @GetMapping("/list/user/{userId}")
    public Response<List<SeckillOrder>> getSeckillOrderByUserId(@PathVariable Long userId) {
        return ResponseUtils.success(seckillOrderService.getSeckillOrderByUserId(userId));
    }

    @GetMapping("/list/activity/{activityId}")
    public Response<List<SeckillOrder>> getSeckillOrderByActivityId(@PathVariable Long activityId) {
        return ResponseUtils.success(seckillOrderService.getSeckillOrderByActivityId(activityId));
    }

}