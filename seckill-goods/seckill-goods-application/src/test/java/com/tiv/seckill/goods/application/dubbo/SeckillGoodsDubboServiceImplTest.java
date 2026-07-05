package com.tiv.seckill.goods.application.dubbo;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.goods.application.service.SeckillGoodsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeckillGoodsDubboServiceImplTest {

    private static final String TRY_KEY = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.GOODS);
    private static final String CONFIRM_KEY = Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.GOODS);
    private static final String CANCEL_KEY = Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.GOODS);

    @Mock
    private SeckillGoodsService seckillGoodsService;

    @Mock
    private DistributedCacheService distributedCacheService;

    @InjectMocks
    private SeckillGoodsDubboServiceImpl seckillGoodsDubboService;

    @Test
    void decreaseAvailableStockRemovesTryRecordWhenStockDeductFails() {
        Long goodsId = 1L;
        Integer count = 2;
        Long txId = 3L;
        when(distributedCacheService.inSet(TRY_KEY, txId)).thenReturn(false);
        when(distributedCacheService.inSet(CONFIRM_KEY, txId)).thenReturn(false);
        when(distributedCacheService.inSet(CANCEL_KEY, txId)).thenReturn(false);
        when(distributedCacheService.addSet(TRY_KEY, txId)).thenReturn(1L);
        when(seckillGoodsService.decreaseAvailableStock(goodsId, count)).thenReturn(false);

        assertThrows(BusinessException.class, () -> seckillGoodsDubboService.decreaseAvailableStock(goodsId, count, txId));

        verify(distributedCacheService).removeSet(TRY_KEY, txId);
    }

    @Test
    void decreaseAvailableStockKeepsTryRecordWhenStockDeductSucceeds() {
        Long goodsId = 1L;
        Integer count = 2;
        Long txId = 3L;
        when(distributedCacheService.inSet(TRY_KEY, txId)).thenReturn(false);
        when(distributedCacheService.inSet(CONFIRM_KEY, txId)).thenReturn(false);
        when(distributedCacheService.inSet(CANCEL_KEY, txId)).thenReturn(false);
        when(distributedCacheService.addSet(TRY_KEY, txId)).thenReturn(1L);
        when(seckillGoodsService.decreaseAvailableStock(goodsId, count)).thenReturn(true);

        assertTrue(seckillGoodsDubboService.decreaseAvailableStock(goodsId, count, txId));

        verify(distributedCacheService, never()).removeSet(TRY_KEY, txId);
    }
}
