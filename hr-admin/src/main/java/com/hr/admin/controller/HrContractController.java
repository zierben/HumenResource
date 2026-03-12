package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrContract;
import com.hr.admin.service.HrContractService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class HrContractController {
    
    private final HrContractService hrContractService;
    
    @GetMapping
    public Result<Page<HrContract>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String contractName,
            @RequestParam(required = false) Integer status) {
        Page<HrContract> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrContract> wrapper = new LambdaQueryWrapper<>();
        if (contractName != null) {
            wrapper.like(HrContract::getContractName, contractName);
        }
        if (status != null) {
            wrapper.eq(HrContract::getStatus, status);
        }
        wrapper.orderByDesc(HrContract::getCreateTime);
        return Result.success(hrContractService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrContract> getById(@PathVariable Long id) {
        return Result.success(hrContractService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrContract hrContract) {
        hrContractService.save(hrContract);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrContract hrContract) {
        hrContract.setId(id);
        hrContractService.updateById(hrContract);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrContractService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/expiring")
    public Result<List<HrContract>> getExpiring(@RequestParam(defaultValue = "30") Integer days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        
        List<HrContract> expiringContracts = hrContractService.lambdaQuery()
                .eq(HrContract::getStatus, 1)
                .between(HrContract::getEndDate, today, endDate)
                .orderByAsc(HrContract::getEndDate)
                .list();
        
        return Result.success(expiringContracts);
    }
    
    @GetMapping("/expired")
    public Result<List<HrContract>> getExpired() {
        LocalDate today = LocalDate.now();
        
        List<HrContract> expiredContracts = hrContractService.lambdaQuery()
                .lt(HrContract::getEndDate, today)
                .in(HrContract::getStatus, 1, 2)
                .orderByAsc(HrContract::getEndDate)
                .list();
        
        return Result.success(expiredContracts);
    }
}
