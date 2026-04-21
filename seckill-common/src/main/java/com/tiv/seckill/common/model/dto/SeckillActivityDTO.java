package com.tiv.seckill.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动 DTO
 */
@Data
public class SeckillActivityDTO implements Serializable {

    /**
     * 秒杀活动 id
     */
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 活动状态 0:已发布 1:上线 2:下线
     */
    private Integer status;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 版本号
     */
    private Long version;

    @Serial
    private static final long serialVersionUID = 1L;

}