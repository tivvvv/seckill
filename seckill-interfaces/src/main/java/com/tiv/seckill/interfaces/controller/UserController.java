package com.tiv.seckill.interfaces.controller;

import com.tiv.seckill.application.service.UserService;
import com.tiv.seckill.domain.model.User;
import com.tiv.seckill.domain.response.Response;
import com.tiv.seckill.domain.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", originPatterns = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    public Response<User> getUser(@RequestParam(value = "name") String name) {
        return ResponseUtils.success(userService.getUserByUserName(name));
    }

}