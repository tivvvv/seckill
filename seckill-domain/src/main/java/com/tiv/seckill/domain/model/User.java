package com.tiv.seckill.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户
 *
 * @TableName user
 */
@Data
@TableName(value = "user")
public class User implements Serializable {

    /**
     * 用户id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 状态 1:正常 2:冻结
     */
    @TableField(value = "status")
    private Integer status;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}