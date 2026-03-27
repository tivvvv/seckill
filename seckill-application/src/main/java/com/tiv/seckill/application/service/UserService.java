package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.UserLoginDTO;
import com.tiv.seckill.domain.model.User;

public interface UserService {

    String login(UserLoginDTO userLoginDTO);

    User getUserByUserId(Long userId);

}