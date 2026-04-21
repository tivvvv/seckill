package com.tiv.seckill.user.infra.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiv.seckill.user.domain.model.User;
import com.tiv.seckill.user.domain.repository.UserRepository;
import com.tiv.seckill.user.infra.mapper.UserMapper;
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