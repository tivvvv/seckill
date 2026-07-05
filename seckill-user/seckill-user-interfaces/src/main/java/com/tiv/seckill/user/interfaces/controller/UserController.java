package com.tiv.seckill.user.interfaces.controller;

import com.tiv.seckill.common.model.dto.UserLoginDTO;
import com.tiv.seckill.common.response.Response;
import com.tiv.seckill.common.response.ResponseUtils;
import com.tiv.seckill.user.application.service.UserService;
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

    @PostMapping("/register")
    public Response<String> register(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        return ResponseUtils.success(userService.register(userLoginDTO));
    }

}
