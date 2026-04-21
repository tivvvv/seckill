package com.tiv.seckill.order.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.tiv.seckill.order", "com.tiv.seckill.common"})
@EnableDubbo(scanBasePackages = "com.tiv.seckill.order")
public class SeckillOrderStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillOrderStarter.class, args);
    }

}