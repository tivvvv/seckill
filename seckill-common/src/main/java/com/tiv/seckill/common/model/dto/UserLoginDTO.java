package com.tiv.seckill.common.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户登录 DTO
 */
@Data
public class UserLoginDTO implements Serializable {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    private static final long serialVersionUID = 1L;

}