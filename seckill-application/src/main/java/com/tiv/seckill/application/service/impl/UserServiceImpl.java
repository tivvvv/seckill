package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.service.UserService;
import com.tiv.seckill.domain.model.User;
import com.tiv.seckill.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserByUserName(String userName) {
        return userRepository.getUserByUserName(userName);
    }

}