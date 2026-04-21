-- lua逻辑: 首先判断商品库存是否存在,不存在则返回-1;如果传入的要扣减的库存数量小于或等于0,则返回-3;如果库存不足则返回-2,扣减库存成功,则返回1
-- 两个入参:
-- KEYS[1]: 商品库存的key
-- ARGV[1]: 商品库存的扣减数量
local stock = redis.call('get', KEYS[1])

-- 商品库存不存在
if not stock then
    return -1
end

-- 获取当前库存和要扣减的库存数量
local curStock = tonumber(stock)
local paramStock = tonumber(ARGV[1])

-- 参数不能小于或等于0
if paramStock <= 0 then
    return -3
end

-- 库存不足
if curStock < paramStock then
    return -2
end

redis.call('decrby', KEYS[1], paramStock)
return 1