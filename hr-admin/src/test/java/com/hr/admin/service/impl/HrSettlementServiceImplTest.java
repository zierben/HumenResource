package com.hr.admin.service.impl;

import com.hr.admin.constant.HrConstants;
import com.hr.admin.entity.HrSettlement;
import com.hr.admin.mapper.HrSettlementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrSettlementServiceImplTest {

    @Mock
    private HrSettlementMapper hrSettlementMapper;

    private HrSettlementServiceImpl hrSettlementService;

    @BeforeEach
    void setUp() {
        hrSettlementService = new HrSettlementServiceImpl(null, null);
        ReflectionTestUtils.setField(hrSettlementService, "baseMapper", hrSettlementMapper);
    }

    @Test
    @DisplayName("HR确认结算")
    void testHrConfirm() {
        when(hrSettlementMapper.updateById(any(HrSettlement.class))).thenReturn(1);

        hrSettlementService.hrConfirm(1L);

        ArgumentCaptor<HrSettlement> captor = ArgumentCaptor.forClass(HrSettlement.class);
        verify(hrSettlementMapper).updateById(captor.capture());
        
        HrSettlement settlement = captor.getValue();
        assertEquals(1L, settlement.getId());
        assertEquals(HrConstants.SettlementStatus.HR_CONFIRMED, settlement.getStatus());
        assertNotNull(settlement.getUpdateTime());
    }

    @Test
    @DisplayName("PM确认结算")
    void testPmConfirm() {
        when(hrSettlementMapper.updateById(any(HrSettlement.class))).thenReturn(1);

        hrSettlementService.pmConfirm(1L);

        ArgumentCaptor<HrSettlement> captor = ArgumentCaptor.forClass(HrSettlement.class);
        verify(hrSettlementMapper).updateById(captor.capture());
        
        HrSettlement settlement = captor.getValue();
        assertEquals(1L, settlement.getId());
        assertEquals(HrConstants.SettlementStatus.PM_CONFIRMED, settlement.getStatus());
    }

    @Test
    @DisplayName("财务确认结算")
    void testFinanceConfirm() {
        when(hrSettlementMapper.updateById(any(HrSettlement.class))).thenReturn(1);

        hrSettlementService.financeConfirm(1L);

        ArgumentCaptor<HrSettlement> captor = ArgumentCaptor.forClass(HrSettlement.class);
        verify(hrSettlementMapper).updateById(captor.capture());
        
        HrSettlement settlement = captor.getValue();
        assertEquals(1L, settlement.getId());
        assertEquals(HrConstants.SettlementStatus.FINANCE_CONFIRMED, settlement.getStatus());
    }

    @Test
    @DisplayName("供应商确认结算")
    void testSupplierConfirm() {
        when(hrSettlementMapper.updateById(any(HrSettlement.class))).thenReturn(1);

        hrSettlementService.supplierConfirm(1L, "确认无误");

        ArgumentCaptor<HrSettlement> captor = ArgumentCaptor.forClass(HrSettlement.class);
        verify(hrSettlementMapper).updateById(captor.capture());
        
        HrSettlement settlement = captor.getValue();
        assertEquals(1L, settlement.getId());
        assertEquals(HrConstants.SettlementStatus.SUPPLIER_CONFIRMED, settlement.getStatus());
        assertEquals("确认无误", settlement.getConfirmRemark());
    }

    @Test
    @DisplayName("支付结算")
    void testPay() {
        when(hrSettlementMapper.updateById(any(HrSettlement.class))).thenReturn(1);

        hrSettlementService.pay(1L);

        ArgumentCaptor<HrSettlement> captor = ArgumentCaptor.forClass(HrSettlement.class);
        verify(hrSettlementMapper).updateById(captor.capture());
        
        HrSettlement settlement = captor.getValue();
        assertEquals(1L, settlement.getId());
        assertEquals(HrConstants.SettlementStatus.PAID, settlement.getStatus());
    }

    @Test
    @DisplayName("状态流转顺序验证")
    void testStatusFlow() {
        assertEquals(0, HrConstants.SettlementStatus.PENDING);
        assertEquals(1, HrConstants.SettlementStatus.HR_CONFIRMED);
        assertEquals(2, HrConstants.SettlementStatus.PM_CONFIRMED);
        assertEquals(3, HrConstants.SettlementStatus.FINANCE_CONFIRMED);
        assertEquals(4, HrConstants.SettlementStatus.SUPPLIER_CONFIRMED);
        assertEquals(5, HrConstants.SettlementStatus.PAID);
    }
}
