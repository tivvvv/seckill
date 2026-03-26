package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.UserLoginDTO;

public interface UserService {

    String login(UserLoginDTO userLoginDTO);

}