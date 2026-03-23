package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.model.User;

public interface UserService {

    User getUserByUserName(String userName);

}