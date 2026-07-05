package com.tiv.seckill.user.application.service.impl;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.UserLoginDTO;
import com.tiv.seckill.common.model.enums.UserStatusEnum;
import com.tiv.seckill.common.util.id.SnowFlakeFactory;
import com.tiv.seckill.common.util.shiro.CommonUtil;
import com.tiv.seckill.common.util.shiro.JwtUtil;
import com.tiv.seckill.user.application.service.UserService;
import com.tiv.seckill.user.domain.model.User;
import com.tiv.seckill.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private DistributedCacheService distributedCacheService;

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

        return loginUser(user);
    }

    @Override
    public String register(UserLoginDTO userLoginDTO) {
        String userName = userLoginDTO.getUserName();
        String password = userLoginDTO.getPassword();

        User existUser = userRepository.getUserByUserName(userName);
        if (existUser != null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户名已存在");
        }

        User user = User.builder()
                .id(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .userName(userName)
                .password(CommonUtil.encryptPassword(password, userName))
                .status(UserStatusEnum.NORMAL.getCode())
                .build();

        try {
            userRepository.saveUser(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户名已存在");
        }

        return loginUser(user);
    }

    private String loginUser(User user) {
        String token = JwtUtil.sign(user.getId());
        String key = Constants.getKey(Constants.USER_LOGIN_KEY_PREFIX, String.valueOf(user.getId()));
        distributedCacheService.put(key, user);
        return token;
    }

}
