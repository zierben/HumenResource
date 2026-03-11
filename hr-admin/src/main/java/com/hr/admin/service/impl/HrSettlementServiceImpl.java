package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.constant.HrConstants;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrSettlement;
import com.hr.admin.entity.HrWorkHours;
import com.hr.admin.mapper.HrSettlementMapper;
import com.hr.admin.service.HrPersonnelService;
import com.hr.admin.service.HrSettlementService;
import com.hr.admin.service.HrWorkHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrSettlementServiceImpl extends ServiceImpl<HrSettlementMapper, HrSettlement> implements HrSettlementService {
    
    private final HrPersonnelService hrPersonnelService;
    private final HrWorkHoursService hrWorkHoursService;
    
    @Override
    @Transactional
    public void generateSettlement(Integer year, Integer month) {
        log.info("开始生成结算账单: {}年{}月", year, month);
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        LambdaQueryWrapper<HrWorkHours> whWrapper = new LambdaQueryWrapper<>();
        whWrapper.eq(HrWorkHours::getStatus, "APPROVED");
        whWrapper.ge(HrWorkHours::getWorkDate, startDate);
        whWrapper.le(HrWorkHours::getWorkDate, endDate);
        List<HrWorkHours> workHoursList = hrWorkHoursService.list(whWrapper);
        
        if (workHoursList.isEmpty()) {
            log.warn("没有已审核的工时记录，无法生成账单");
            return;
        }
        
        List<Long> personnelIds = workHoursList.stream()
                .map(HrWorkHours::getPersonnelId)
                .distinct()
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<HrPersonnel> pWrapper = new LambdaQueryWrapper<>();
        pWrapper.in(HrPersonnel::getId, personnelIds);
        List<HrPersonnel> personnelList = hrPersonnelService.list(pWrapper);
        
        Map<Long, HrPersonnel> personnelMap = personnelList.stream()
                .collect(Collectors.toMap(HrPersonnel::getId, p -> p));
        
        Map<String, List<HrWorkHours>> groupedWorkHours = workHoursList.stream()
                .filter(wh -> personnelMap.containsKey(wh.getPersonnelId()))
                .collect(Collectors.groupingBy(wh -> {
                    HrPersonnel personnel = personnelMap.get(wh.getPersonnelId());
                    Long supplierId = personnel.getSupplierId();
                    Long projectId = wh.getProjectId() != null ? wh.getProjectId() : personnel.getProjectId();
                    return supplierId + "_" + projectId;
                }));
        
        int index = 1;
        
        for (Map.Entry<String, List<HrWorkHours>> entry : groupedWorkHours.entrySet()) {
            String[] keys = entry.getKey().split("_");
            Long supplierId = Long.parseLong(keys[0]);
            Long projectId = keys.length > 1 && !keys[1].equals("null") ? Long.parseLong(keys[1]) : null;
            
            List<HrWorkHours> groupWorkHours = entry.getValue();
            
            BigDecimal totalHours = groupWorkHours.stream()
                    .map(HrWorkHours::getHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal validDays = totalHours.divide(new BigDecimal("8"), 2, RoundingMode.HALF_UP);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (HrWorkHours wh : groupWorkHours) {
                HrPersonnel personnel = personnelMap.get(wh.getPersonnelId());
                if (personnel != null && personnel.getDailyRate() != null) {
                    BigDecimal hours = wh.getHours() != null ? wh.getHours() : BigDecimal.ZERO;
                    BigDecimal rate = personnel.getDailyRate();
                    totalAmount = totalAmount.add(rate.multiply(hours).divide(new BigDecimal("8"), 2, RoundingMode.HALF_UP));
                }
            }
            
            String code = "SET" + year + String.format("%02d", month) 
                    + String.format("%03d", index++);
            
            HrSettlement settlement = new HrSettlement();
            settlement.setSettlementCode(code);
            settlement.setSettlementYear(year);
            settlement.setSettlementMonth(month);
            settlement.setSupplierId(supplierId);
            settlement.setProjectId(projectId);
            settlement.setValidDays(validDays);
            settlement.setTotalAmount(totalAmount);
            settlement.setDeductionAmount(BigDecimal.ZERO);
            settlement.setFinalAmount(totalAmount);
            settlement.setStatus(HrConstants.SettlementStatus.PENDING);
            settlement.setCreateTime(LocalDateTime.now());
            settlement.setUpdateTime(LocalDateTime.now());
            save(settlement);
            
            log.info("生成账单: {}, 供应商ID: {}, 项目ID: {}, 金额: {}", code, supplierId, projectId, totalAmount);
        }
        
        log.info("账单生成完成，共生成 {} 条", groupedWorkHours.size());
    }
    
    @Override
    @Transactional
    public void hrConfirm(Long id) {
        HrSettlement settlement = new HrSettlement();
        settlement.setId(id);
        settlement.setStatus(HrConstants.SettlementStatus.HR_CONFIRMED);
        settlement.setUpdateTime(LocalDateTime.now());
        updateById(settlement);
    }
    
    @Override
    @Transactional
    public void pmConfirm(Long id) {
        HrSettlement settlement = new HrSettlement();
        settlement.setId(id);
        settlement.setStatus(HrConstants.SettlementStatus.PM_CONFIRMED);
        settlement.setUpdateTime(LocalDateTime.now());
        updateById(settlement);
    }
    
    @Override
    @Transactional
    public void financeConfirm(Long id) {
        HrSettlement settlement = new HrSettlement();
        settlement.setId(id);
        settlement.setStatus(HrConstants.SettlementStatus.FINANCE_CONFIRMED);
        settlement.setUpdateTime(LocalDateTime.now());
        updateById(settlement);
    }
    
    @Override
    @Transactional
    public void supplierConfirm(Long id, String remark) {
        HrSettlement settlement = new HrSettlement();
        settlement.setId(id);
        settlement.setStatus(HrConstants.SettlementStatus.SUPPLIER_CONFIRMED);
        settlement.setConfirmRemark(remark);
        settlement.setUpdateTime(LocalDateTime.now());
        updateById(settlement);
    }
    
    @Override
    @Transactional
    public void pay(Long id) {
        HrSettlement settlement = new HrSettlement();
        settlement.setId(id);
        settlement.setStatus(HrConstants.SettlementStatus.PAID);
        settlement.setUpdateTime(LocalDateTime.now());
        updateById(settlement);
    }
}
