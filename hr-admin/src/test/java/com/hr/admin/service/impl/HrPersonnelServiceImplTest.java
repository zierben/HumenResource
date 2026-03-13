package com.hr.admin.service.impl;

import com.hr.admin.constant.HrConstants;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.mapper.HrPersonnelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrPersonnelServiceImplTest {

    @Mock
    private HrPersonnelMapper hrPersonnelMapper;

    private HrPersonnelServiceImpl hrPersonnelService;

    private HrPersonnel testPersonnel;

    @BeforeEach
    void setUp() {
        hrPersonnelService = new HrPersonnelServiceImpl();
        ReflectionTestUtils.setField(hrPersonnelService, "baseMapper", hrPersonnelMapper);
        
        testPersonnel = new HrPersonnel();
        testPersonnel.setName("张三");
        testPersonnel.setSupplierId(1L);
        testPersonnel.setProjectId(1L);
        testPersonnel.setPositionName("前端开发工程师");
        testPersonnel.setDailyRate(BigDecimal.valueOf(800));
    }

    @Test
    @DisplayName("保存人员-生成人员编码")
    void testSavePersonnel_GenerateCode() {
        when(hrPersonnelMapper.insert(any(HrPersonnel.class))).thenReturn(1);

        hrPersonnelService.savePersonnel(testPersonnel);

        ArgumentCaptor<HrPersonnel> captor = ArgumentCaptor.forClass(HrPersonnel.class);
        verify(hrPersonnelMapper).insert(captor.capture());
        
        HrPersonnel savedPersonnel = captor.getValue();
        assertNotNull(savedPersonnel.getPersonnelCode());
        assertTrue(savedPersonnel.getPersonnelCode().startsWith("PER"));
        assertEquals(HrConstants.PersonnelStatus.PENDING_ENTRY, savedPersonnel.getStatus());
        assertNotNull(savedPersonnel.getCreateTime());
        assertNotNull(savedPersonnel.getUpdateTime());
    }

    @Test
    @DisplayName("人员入场-状态变更")
    void testEntry() {
        when(hrPersonnelMapper.updateById(any(HrPersonnel.class))).thenReturn(1);

        hrPersonnelService.entry(1L);

        ArgumentCaptor<HrPersonnel> captor = ArgumentCaptor.forClass(HrPersonnel.class);
        verify(hrPersonnelMapper).updateById(captor.capture());
        
        HrPersonnel updatedPersonnel = captor.getValue();
        assertEquals(1L, updatedPersonnel.getId());
        assertEquals(HrConstants.PersonnelStatus.ON_DUTY, updatedPersonnel.getStatus());
        assertEquals(LocalDate.now(), updatedPersonnel.getEntryDate());
        assertNotNull(updatedPersonnel.getUpdateTime());
    }

    @Test
    @DisplayName("人员离场-状态变更")
    void testExit() {
        when(hrPersonnelMapper.updateById(any(HrPersonnel.class))).thenReturn(1);

        hrPersonnelService.exit(1L);

        ArgumentCaptor<HrPersonnel> captor = ArgumentCaptor.forClass(HrPersonnel.class);
        verify(hrPersonnelMapper).updateById(captor.capture());
        
        HrPersonnel updatedPersonnel = captor.getValue();
        assertEquals(1L, updatedPersonnel.getId());
        assertEquals(HrConstants.PersonnelStatus.OFF_DUTY, updatedPersonnel.getStatus());
        assertNotNull(updatedPersonnel.getUpdateTime());
    }

    @Test
    @DisplayName("人员调配-项目变更")
    void testTransfer() {
        when(hrPersonnelMapper.updateById(any(HrPersonnel.class))).thenReturn(1);

        hrPersonnelService.transfer(1L, 2L);

        ArgumentCaptor<HrPersonnel> captor = ArgumentCaptor.forClass(HrPersonnel.class);
        verify(hrPersonnelMapper).updateById(captor.capture());
        
        HrPersonnel updatedPersonnel = captor.getValue();
        assertEquals(1L, updatedPersonnel.getId());
        assertEquals(2L, updatedPersonnel.getProjectId());
        assertEquals(HrConstants.PersonnelStatus.TRANSFER, updatedPersonnel.getStatus());
        assertNotNull(updatedPersonnel.getUpdateTime());
    }

    @Test
    @DisplayName("人员编码格式验证")
    void testPersonnelCodeFormat() {
        when(hrPersonnelMapper.insert(any(HrPersonnel.class))).thenReturn(1);

        hrPersonnelService.savePersonnel(testPersonnel);

        ArgumentCaptor<HrPersonnel> captor = ArgumentCaptor.forClass(HrPersonnel.class);
        verify(hrPersonnelMapper).insert(captor.capture());
        
        String code = captor.getValue().getPersonnelCode();
        assertTrue(code.matches("PER\\d{11}"), "人员编码格式应为PER+8位日期+3位序号");
    }
}
