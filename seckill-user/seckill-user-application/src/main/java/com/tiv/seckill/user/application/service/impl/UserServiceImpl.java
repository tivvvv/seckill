package com.tiv.seckill.user.application.service.impl;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.UserLoginDTO;
import com.tiv.seckill.common.util.shiro.CommonUtil;
import com.tiv.seckill.common.util.shiro.JwtUtil;
import com.tiv.seckill.user.application.service.UserService;
import com.tiv.seckill.user.domain.model.User;
import com.tiv.seckill.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        // 3. 生成 token
        String token = JwtUtil.sign(user.getId());
        String key = Constants.getKey(Constants.USER_LOGIN_KEY_PREFIX, String.valueOf(user.getId()));

        // 4. 缓存 token
        distributedCacheService.put(key, user);

        return token;
    }

}