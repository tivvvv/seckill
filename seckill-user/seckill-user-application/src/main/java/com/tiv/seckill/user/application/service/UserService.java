package com.tiv.seckill.user.application.service;

import com.tiv.seckill.common.model.dto.UserLoginDTO;

public interface UserService {

    String login(UserLoginDTO userLoginDTO);

}