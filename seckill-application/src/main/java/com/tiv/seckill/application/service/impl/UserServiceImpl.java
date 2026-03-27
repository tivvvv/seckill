package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.service.RedisService;
import com.tiv.seckill.application.service.UserService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.constants.Constants;
import com.tiv.seckill.domain.dto.UserLoginDTO;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.User;
import com.tiv.seckill.domain.repository.UserRepository;
import com.tiv.seckill.infra.util.CommonUtil;
import com.tiv.seckill.infra.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        String userName = userLoginDTO.getUserName();
        String password = userLoginDTO.getPassword();

        // 1. 验证用户
        User user = userRepository.getUserByUserName(userName);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "用户不存在");
        }

        // 2. 验证密码
        String encrypted = CommonUtil.encryptPassword(password, userName);
        if (!encrypted.equals(user.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "密码错误");
        }

        // 3. 生成 token
        String token = JwtUtil.sign(user.getId());
        String key = Constants.getKey(Constants.USER_LOGIN_KEY_PREFIX, String.valueOf(user.getId()));

        // 4. 缓存 token
        redisService.set(key, user);

        return token;
    }

    @Override
    public User getUserByUserId(Long userId) {
        String key = Constants.getKey(Constants.USER_LOGIN_KEY_PREFIX, String.valueOf(userId));
        return (User) redisService.get(key);
    }

}