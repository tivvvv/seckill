package com.tiv.seckill.activity.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.tiv.seckill.activity", "com.tiv.seckill.common"})
@EnableDubbo(scanBasePackages = "com.tiv.seckill.activity")
public class SeckillActivityStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillActivityStarter.class, args);
    }

}