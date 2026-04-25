package com.tiv.seckill.common.constants;

public class Constants {

    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";

    public static final String ORDER_LOCK_KEY_PREFIX = "order:lock:";

    public static final String ORDER_TRY_KEY_PREFIX = "order:try:";

    public static final String ORDER_CONFIRM_KEY_PREFIX = "order:confirm:";

    public static final String ORDER_CANCEL_KEY_PREFIX = "order:cancel:";

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

    /**
     * 秒杀商品列表缓存key
     */
    public static final String SECKILL_GOODS_LIST_CACHE_KEY = "SECKILL_GOODS_LIST_CACHE_KEY";

    /**
     * 秒杀商品缓存key
     */
    public static final String SECKILL_GOODS_CACHE_KEY = "SECKILL_GOODS_CACHE_KEY";

    /**
     * 秒杀商品库存缓存key
     */
    public static final String SECKILL_GOODS_STOCK_CACHE_KEY = "SECKILL_GOODS_STOCK_CACHE_KEY";

    /**
     * LUA脚本运行结果 商品库存不存在
     */
    public static final int LUA_RESULT_GOODS_STOCK_NOT_EXISTS = -1;

    /**
     * LUA脚本运行结果 库存不足
     */
    public static final int LUA_RESULT_GOODS_STOCK_LT_ZERO = -2;

    /**
     * LUA脚本运行结果 参数错误
     */
    public static final int LUA_RESULT_GOODS_PARAMS_ERROR = -3;

    public static final String GOODS = "goods";

}