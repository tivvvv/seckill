package com.tiv.seckill.user.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.tiv.seckill.user", "com.tiv.seckill.common"})
public class SeckillUserStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillUserStarter.class, args);
    }

}