package com.tiv.seckill.domain.repository;

import com.tiv.seckill.domain.model.User;

public interface UserRepository {

    User getUserByUserName(String userName);

}