package com.tiv.seckill.goods.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.tiv.seckill.goods", "com.tiv.seckill.common"})
@EnableDubbo(scanBasePackages = "com.tiv.seckill.goods")
public class SeckillGoodsStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillGoodsStarter.class, args);
    }

}