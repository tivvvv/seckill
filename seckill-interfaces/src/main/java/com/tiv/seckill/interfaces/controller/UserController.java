package com.tiv.seckill.interfaces.controller;

import com.tiv.seckill.application.service.UserService;
import com.tiv.seckill.domain.dto.UserLoginDTO;
import com.tiv.seckill.domain.response.Response;
import com.tiv.seckill.domain.response.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", originPatterns = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<String> login(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        return ResponseUtils.success(userService.login(userLoginDTO));
    }

}