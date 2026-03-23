package com.tiv.seckill.infra.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiv.seckill.domain.model.User;
import com.tiv.seckill.domain.repository.UserRepository;
import com.tiv.seckill.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUserName(String userName) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("user_name", userName));
    }

}