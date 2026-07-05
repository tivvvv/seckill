package com.tiv.seckill.common.cache.distributed.redis;

import com.alibaba.fastjson2.JSON;
import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的分布式缓存实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "cache.distributed.type", havingValue = "redis")
public class RedisCacheServiceImpl implements DistributedCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 初始化库存脚本.
     */
    private static final DefaultRedisScript<Long> INIT_GOODS_STOCK_SCRIPT;

    /**
     * 增加库存脚本.
     */
    private static final DefaultRedisScript<Long> INCREMENT_GOODS_STOCK_SCRIPT;

    /**
     * 扣减库存脚本.
     */
    private static final DefaultRedisScript<Long> DECREMENT_GOODS_STOCK_SCRIPT;

    static {
        // 初始化库存
        INIT_GOODS_STOCK_SCRIPT = new DefaultRedisScript<>();
        INIT_GOODS_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/init_goods_stock.lua"));
        INIT_GOODS_STOCK_SCRIPT.setResultType(Long.class);

        // 增加库存
        INCREMENT_GOODS_STOCK_SCRIPT = new DefaultRedisScript<>();
        INCREMENT_GOODS_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/increment_goods_stock.lua"));
        INCREMENT_GOODS_STOCK_SCRIPT.setResultType(Long.class);

        // 扣减库存
        DECREMENT_GOODS_STOCK_SCRIPT = new DefaultRedisScript<>();
        DECREMENT_GOODS_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/decrement_goods_stock.lua"));
        DECREMENT_GOODS_STOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void put(String key, Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, Object value, long timeout) {
        this.put(key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public String getString(String key) {
        Object value = this.getObject(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    @Override
    public <T> T getObject(String key, Class<T> targetClass) {
        Object value = this.getObject(key);
        if (value == null) {
            return null;
        }
        try {
            return JSON.parseObject((String) value, targetClass);
        } catch (Exception e) {
            log.error("Failed to parse object from redis cache, key: {}, value: {}", key, value, e);
            return null;
        }
    }

    @Override
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean delete(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long addSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long removeSet(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public Boolean inSet(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long increment(String key, Long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decrement(String key, Long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    @Override
    public Long initByLua(String key, Integer quantity) {
        return redisTemplate.execute(INIT_GOODS_STOCK_SCRIPT, Collections.singletonList(key), quantity);
    }

    @Override
    public Long incrementByLua(String key, Integer quantity) {
        return redisTemplate.execute(INCREMENT_GOODS_STOCK_SCRIPT, Collections.singletonList(key), quantity);
    }

    @Override
    public Long decrementByLua(String key, Integer quantity) {
        return redisTemplate.execute(DECREMENT_GOODS_STOCK_SCRIPT, Collections.singletonList(key), quantity);
    }

    @Override
    public void checkLuaResult(Long result) {
        if (result == Constants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "商品库存不存在");
        }

        if (result == Constants.LUA_RESULT_GOODS_STOCK_LT_ZERO) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "商品库存不足");
        }

        if (result == Constants.LUA_RESULT_GOODS_PARAMS_ERROR) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "商品参数错误");
        }
    }

}