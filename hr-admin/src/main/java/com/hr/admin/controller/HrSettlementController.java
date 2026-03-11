package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrSettlement;
import com.hr.admin.service.HrSettlementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class HrSettlementController {
    
    private final HrSettlementService hrSettlementService;
    
    @GetMapping
    public Result<Page<HrSettlement>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer settlementYear,
            @RequestParam(required = false) Integer settlementMonth,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status) {
        Page<HrSettlement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrSettlement> wrapper = new LambdaQueryWrapper<>();
        if (settlementYear != null) {
            wrapper.eq(HrSettlement::getSettlementYear, settlementYear);
        }
        if (settlementMonth != null) {
            wrapper.eq(HrSettlement::getSettlementMonth, settlementMonth);
        }
        if (supplierId != null) {
            wrapper.eq(HrSettlement::getSupplierId, supplierId);
        }
        if (status != null) {
            wrapper.eq(HrSettlement::getStatus, status);
        }
        wrapper.orderByDesc(HrSettlement::getCreateTime);
        return Result.success(hrSettlementService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrSettlement> getById(@PathVariable Long id) {
        return Result.success(hrSettlementService.getById(id));
    }
    
    @PostMapping("/generate")
    public Result<Void> generate(@RequestParam Integer year, @RequestParam Integer month) {
        hrSettlementService.generateSettlement(year, month);
        return Result.success();
    }
    
    @PostMapping("/{id}/hr-confirm")
    public Result<Void> hrConfirm(@PathVariable Long id) {
        hrSettlementService.hrConfirm(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/pm-confirm")
    public Result<Void> pmConfirm(@PathVariable Long id) {
        hrSettlementService.pmConfirm(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/finance-confirm")
    public Result<Void> financeConfirm(@PathVariable Long id) {
        hrSettlementService.financeConfirm(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/supplier-confirm")
    public Result<Void> supplierConfirm(@PathVariable Long id, @RequestParam(required = false) String remark) {
        hrSettlementService.supplierConfirm(id, remark);
        return Result.success();
    }
    
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        hrSettlementService.pay(id);
        return Result.success();
    }
}
