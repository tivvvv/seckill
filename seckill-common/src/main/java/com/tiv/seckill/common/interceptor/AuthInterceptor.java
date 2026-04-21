package com.tiv.seckill.common.interceptor;

import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.util.shiro.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object attribute = request.getAttribute(USER_ID);
        if (attribute != null) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN_HEADER_NAME);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCodeEnum.NOT_LOGIN_ERROR);
        }

        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_LOGIN_ERROR);
        }
        HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(request);
        request.setAttribute(USER_ID, userId);
        return true;
    }

}