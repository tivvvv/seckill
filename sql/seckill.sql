DROP TABLE IF EXISTS `seckill_user`;
CREATE TABLE `seckill_user`
(
    `id`        bigint(20)  NOT NULL COMMENT '用户id',
    `user_name` varchar(20) NULL DEFAULT '' COMMENT '用户名',
    `password`  varchar(64) NULL DEFAULT '' COMMENT '密码',
    `status`    int(2)      NULL DEFAULT 1 COMMENT '状态 1:正常,2:冻结',
    PRIMARY KEY (`id`)
) COMMENT = '用户表';

DROP TABLE IF EXISTS `seckill_activity`;
CREATE TABLE `seckill_activity`
(
    `id`            bigint(20)   NOT NULL COMMENT '秒杀活动id',
    `activity_name` varchar(128) NULL DEFAULT '' COMMENT '活动名称',
    `activity_desc` varchar(255) NULL DEFAULT '' COMMENT '活动描述',
    `start_time`    datetime(0)  NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`      datetime(0)  NULL DEFAULT NULL COMMENT '结束时间',
    `status`        int(2)       NULL DEFAULT 0 COMMENT '状态 0:已发布,1:上线,2:下线',
    PRIMARY KEY (`id`)
) COMMENT = '秒杀活动表';

DROP TABLE IF EXISTS `seckill_goods`;
CREATE TABLE `seckill_goods`
(
    `id`              bigint(20)     NOT NULL COMMENT '商品id',
    `goods_name`      varchar(128)   NULL DEFAULT '' COMMENT '商品名称',
    `activity_id`     bigint(20)     NULL DEFAULT 0 COMMENT '活动id',
    `start_time`      datetime(0)    NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`        datetime(0)    NULL DEFAULT NULL COMMENT '结束时间',
    `original_price`  decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品原价格',
    `activity_price`  decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品秒杀价格',
    `initial_stock`   int(10)        NULL DEFAULT 0 COMMENT '商品初始库存',
    `available_stock` int(10)        NULL DEFAULT 0 COMMENT '商品当前可用库存',
    `description`     varchar(255)   NULL DEFAULT '' COMMENT '商品描述',
    `img_url`         varchar(255)   NULL DEFAULT '' COMMENT '商品图片',
    `limit_num`       int(11)        NULL DEFAULT 1 COMMENT '限购个数',
    `status`          int(2)         NULL DEFAULT 0 COMMENT '状态 0:已发布,1:上线,2:下线',
    PRIMARY KEY (`id`)
) COMMENT = '秒杀商品表';

DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order`
(
    `id`             bigint(20)     NOT NULL COMMENT '订单id',
    `user_id`        bigint(20)     NULL DEFAULT 0 COMMENT '用户id',
    `goods_id`       bigint(20)     NULL DEFAULT 0 COMMENT '商品id',
    `goods_name`     varchar(128)   NULL DEFAULT '' COMMENT '商品名称',
    `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
    `quantity`       int(10)        NULL DEFAULT 0 COMMENT '下单商品数量',
    `order_price`    decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
    `activity_id`    bigint(20)     NULL DEFAULT 0 COMMENT '活动id',
    `status`         int(2)         NULL DEFAULT 0 COMMENT '订单状态 -1:已删除,0:已取消,1:已创建,2:已支付',
    `create_time`    datetime(0)    NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT = '秒杀订单表';