package com.tiv.seckill.domain.constants;

public class Constants {

    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";

    public static String getKey(String prefix, String key) {
        return prefix.concat(key);
    }

    /**
     * token 载荷中存放的信息 userId
     */
    public static final String TOKEN_CLAIM = "userId";

    /**
     * token 过期时间 默认为7天
     */
    public static final Long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * token 请求头名称
     */
    public static final String TOKEN_HEADER_NAME = "access-token";

    /**
     * JWT 密钥
     */
    public static final String JWT_SECRET = "a1b2c3d4e5f6g7h8i9j10";

    /**
     * 秒杀活动列表缓存key
     */
    public static final String SECKILL_ACTIVITY_LIST_CACHE_KEY = "SECKILL_ACTIVITY_LIST_CACHE_KEY";

    /**
     * 秒杀活动缓存key
     */
    public static final String SECKILL_ACTIVITY_CACHE_KEY = "SECKILL_ACTIVITY_CACHE_KEY";

}