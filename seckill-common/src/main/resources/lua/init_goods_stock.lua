-- lua逻辑: 首先判断传入的初始化库存数是否大于0,不符则返回-3;初始化库存成功,返回1
-- 两个入参:
-- KEYS[1]: 商品库存的key
-- ARGV[1]: 商品库存的初始化数量

local paramStock = tonumber(ARGV[1])

-- 参数不能小于或等于0
if paramStock <= 0 then
    return -3
end

redis.call('set', KEYS[1], paramStock)
return 1