package com.tiv.seckill.order.application.place.impl;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.lock.DistributedLock;
import com.tiv.seckill.common.lock.DistributedLockFactory;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.common.model.enums.SeckillGoodsStatusEnum;
import com.tiv.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.domain.model.SeckillOrder;
import com.tiv.seckill.order.domain.service.SeckillOrderDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeckillPlaceOrderServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long GOODS_ID = 2L;
    private static final Long ACTIVITY_ID = 3L;
    private static final Long VERSION = 4L;
    private static final Integer QUANTITY = 2;
    private static final Long TX_ID = 5L;
    private static final String TRY_KEY = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.ORDER);
    private static final String CONFIRM_KEY = Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.ORDER);
    private static final String CANCEL_KEY = Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.ORDER);
    private static final String STOCK_CACHE_KEY = Constants.getKey(Constants.SECKILL_GOODS_STOCK_CACHE_KEY, String.valueOf(GOODS_ID));
    private static final String LOCK_KEY = Constants.getKey(Constants.ORDER_LOCK_KEY_PREFIX, String.valueOf(GOODS_ID));

    @Mock
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Mock
    private SeckillOrderDomainService seckillOrderDomainService;

    @Mock
    private DistributedCacheService distributedCacheService;

    @Mock
    private DistributedLockFactory distributedLockFactory;

    @Mock
    private DistributedLock distributedLock;

    @InjectMocks
    private SeckillPlaceOrderLuaServiceImpl luaPlaceOrderService;

    @InjectMocks
    private SeckillPlaceOrderLockServiceImpl lockPlaceOrderService;

    @InjectMocks
    private SeckillPlaceOrderDbServiceImpl dbPlaceOrderService;

    @Test
    void luaPlaceOrderRollsBackTryAndCachedStockWhenGoodsTryReturnsFalse() {
        SeckillOrderCommand command = buildCommand();
        mockOrderTccStatusNotExecuted();
        when(seckillGoodsDubboService.getSeckillGoodsDTO(GOODS_ID, VERSION)).thenReturn(buildGoodsDTO());
        when(distributedCacheService.decrementByLua(STOCK_CACHE_KEY, QUANTITY)).thenReturn(1L);
        when(seckillOrderDomainService.saveSeckillOrder(any(SeckillOrder.class))).thenReturn(true);
        when(seckillGoodsDubboService.decreaseAvailableStock(GOODS_ID, QUANTITY, TX_ID)).thenReturn(false);

        assertThrows(BusinessException.class, () -> luaPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(distributedCacheService).removeSet(TRY_KEY, TX_ID);
        verify(distributedCacheService).incrementByLua(STOCK_CACHE_KEY, QUANTITY);
    }

    @Test
    void luaPlaceOrderRollsBackCachedStockWhenOrderSaveReturnsFalse() {
        SeckillOrderCommand command = buildCommand();
        mockOrderTccStatusNotExecuted();
        when(seckillGoodsDubboService.getSeckillGoodsDTO(GOODS_ID, VERSION)).thenReturn(buildGoodsDTO());
        when(distributedCacheService.decrementByLua(STOCK_CACHE_KEY, QUANTITY)).thenReturn(1L);
        when(seckillOrderDomainService.saveSeckillOrder(any(SeckillOrder.class))).thenReturn(false);

        assertThrows(BusinessException.class, () -> luaPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(distributedCacheService).incrementByLua(STOCK_CACHE_KEY, QUANTITY);
        verify(distributedCacheService, never()).addSet(TRY_KEY, TX_ID);
        verify(seckillGoodsDubboService, never()).decreaseAvailableStock(GOODS_ID, QUANTITY, TX_ID);
    }

    @Test
    void lockPlaceOrderRollsBackTryAndCachedStockWhenGoodsTryReturnsFalse() throws InterruptedException {
        SeckillOrderCommand command = buildCommand();
        mockOrderTccStatusNotExecuted();
        when(seckillGoodsDubboService.getSeckillGoodsDTO(GOODS_ID, VERSION)).thenReturn(buildGoodsDTO());
        when(distributedLockFactory.getDistributedLock(LOCK_KEY)).thenReturn(distributedLock);
        when(distributedLock.tryLock(2L, 5L, TimeUnit.SECONDS)).thenReturn(true);
        when(distributedCacheService.getObject(STOCK_CACHE_KEY, Integer.class)).thenReturn(10);
        when(seckillOrderDomainService.saveSeckillOrder(any(SeckillOrder.class))).thenReturn(true);
        when(seckillGoodsDubboService.decreaseAvailableStock(GOODS_ID, QUANTITY, TX_ID)).thenReturn(false);

        assertThrows(BusinessException.class, () -> lockPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(distributedCacheService).removeSet(TRY_KEY, TX_ID);
        verify(distributedCacheService).increment(STOCK_CACHE_KEY, Long.valueOf(QUANTITY));
        verify(distributedLock).unLock();
    }

    @Test
    void lockPlaceOrderRollsBackCachedStockWhenOrderSaveReturnsFalse() throws InterruptedException {
        SeckillOrderCommand command = buildCommand();
        mockOrderTccStatusNotExecuted();
        when(seckillGoodsDubboService.getSeckillGoodsDTO(GOODS_ID, VERSION)).thenReturn(buildGoodsDTO());
        when(distributedLockFactory.getDistributedLock(LOCK_KEY)).thenReturn(distributedLock);
        when(distributedLock.tryLock(2L, 5L, TimeUnit.SECONDS)).thenReturn(true);
        when(distributedCacheService.getObject(STOCK_CACHE_KEY, Integer.class)).thenReturn(10);
        when(seckillOrderDomainService.saveSeckillOrder(any(SeckillOrder.class))).thenReturn(false);

        assertThrows(BusinessException.class, () -> lockPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(distributedCacheService).increment(STOCK_CACHE_KEY, Long.valueOf(QUANTITY));
        verify(distributedCacheService, never()).addSet(TRY_KEY, TX_ID);
        verify(seckillGoodsDubboService, never()).decreaseAvailableStock(GOODS_ID, QUANTITY, TX_ID);
        verify(distributedLock).unLock();
    }

    @Test
    void cancelMethodRecordsCancelWhenTryNotExecuted() {
        SeckillOrderCommand command = buildCommand();
        when(distributedCacheService.inSet(CANCEL_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(false);

        assertEquals(TX_ID, luaPlaceOrderService.cancelMethod(USER_ID, command, TX_ID));

        verify(distributedCacheService).addSet(CANCEL_KEY, TX_ID);
        verify(seckillOrderDomainService, never()).deleteSeckillOrder(TX_ID);
    }

    @Test
    void confirmMethodThrowsWhenConfirmRecordFails() {
        SeckillOrderCommand command = buildCommand();
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(true);
        when(distributedCacheService.inSet(CONFIRM_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.addSet(CONFIRM_KEY, TX_ID)).thenThrow(new RuntimeException("redis failed"));

        assertThrows(BusinessException.class, () -> luaPlaceOrderService.confirmMethod(USER_ID, command, TX_ID));
    }

    @Test
    void cancelMethodThrowsAndRemovesCancelRecordWhenDeleteFails() {
        SeckillOrderCommand command = buildCommand();
        when(distributedCacheService.inSet(CANCEL_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(true);
        when(distributedCacheService.addSet(CANCEL_KEY, TX_ID)).thenReturn(1L);
        doThrow(new RuntimeException("delete failed")).when(seckillOrderDomainService).deleteSeckillOrder(TX_ID);

        assertThrows(BusinessException.class, () -> luaPlaceOrderService.cancelMethod(USER_ID, command, TX_ID));

        verify(distributedCacheService).removeSet(CANCEL_KEY, TX_ID);
    }

    @Test
    void luaPlaceOrderThrowsWhenCancelRecorded() {
        SeckillOrderCommand command = buildCommand();
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CONFIRM_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CANCEL_KEY, TX_ID)).thenReturn(true);

        assertThrows(BusinessException.class, () -> luaPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(seckillGoodsDubboService, never()).getSeckillGoodsDTO(GOODS_ID, VERSION);
        verify(seckillOrderDomainService, never()).saveSeckillOrder(any(SeckillOrder.class));
    }

    @Test
    void lockPlaceOrderThrowsWhenCancelRecorded() {
        SeckillOrderCommand command = buildCommand();
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CONFIRM_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CANCEL_KEY, TX_ID)).thenReturn(true);

        assertThrows(BusinessException.class, () -> lockPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(seckillGoodsDubboService, never()).getSeckillGoodsDTO(GOODS_ID, VERSION);
        verify(seckillOrderDomainService, never()).saveSeckillOrder(any(SeckillOrder.class));
    }

    @Test
    void dbPlaceOrderThrowsWhenConfirmRecorded() {
        SeckillOrderCommand command = buildCommand();
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CONFIRM_KEY, TX_ID)).thenReturn(true);

        assertThrows(BusinessException.class, () -> dbPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(seckillGoodsDubboService, never()).getSeckillGoodsDTO(GOODS_ID, VERSION);
        verify(seckillOrderDomainService, never()).saveSeckillOrder(any(SeckillOrder.class));
    }

    @Test
    void dbPlaceOrderRemovesTryRecordWhenOrderSaveReturnsFalse() {
        SeckillOrderCommand command = buildCommand();
        mockOrderTccStatusNotExecuted();
        when(seckillGoodsDubboService.getSeckillGoodsDTO(GOODS_ID, VERSION)).thenReturn(buildGoodsDTO());
        when(seckillGoodsDubboService.decreaseAvailableStock(GOODS_ID, QUANTITY, TX_ID)).thenReturn(true);
        when(distributedCacheService.addSet(TRY_KEY, TX_ID)).thenReturn(1L);
        when(seckillOrderDomainService.saveSeckillOrder(any(SeckillOrder.class))).thenReturn(false);

        assertThrows(BusinessException.class, () -> dbPlaceOrderService.placeOrder(USER_ID, command, TX_ID));

        verify(distributedCacheService).removeSet(TRY_KEY, TX_ID);
    }

    private void mockOrderTccStatusNotExecuted() {
        when(distributedCacheService.inSet(TRY_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CONFIRM_KEY, TX_ID)).thenReturn(false);
        when(distributedCacheService.inSet(CANCEL_KEY, TX_ID)).thenReturn(false);
    }

    private SeckillOrderCommand buildCommand() {
        SeckillOrderCommand command = new SeckillOrderCommand();
        command.setGoodsId(GOODS_ID);
        command.setActivityId(ACTIVITY_ID);
        command.setQuantity(QUANTITY);
        command.setVersion(VERSION);
        return command;
    }

    private SeckillGoodsDTO buildGoodsDTO() {
        SeckillGoodsDTO goodsDTO = new SeckillGoodsDTO();
        goodsDTO.setId(GOODS_ID);
        goodsDTO.setActivityId(ACTIVITY_ID);
        goodsDTO.setGoodsName("test goods");
        goodsDTO.setActivityPrice(BigDecimal.ONE);
        goodsDTO.setAvailableStock(10);
        goodsDTO.setLimitNum(5);
        goodsDTO.setStatus(SeckillGoodsStatusEnum.ONLINE.getCode());
        return goodsDTO;
    }

}
