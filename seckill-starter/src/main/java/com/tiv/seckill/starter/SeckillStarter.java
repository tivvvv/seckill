package com.tiv.seckill.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.tiv.seckill"})
public class SeckillStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillStarter.class, args);
    }

}