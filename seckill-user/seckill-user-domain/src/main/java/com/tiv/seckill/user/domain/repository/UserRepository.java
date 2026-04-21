package com.tiv.seckill.user.domain.repository;

import com.tiv.seckill.user.domain.model.User;

public interface UserRepository {

    User getUserByUserName(String userName);

}