package com.hr.admin.controller;

import com.hr.admin.annotation.OperationLog;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrSupplier;
import com.hr.admin.service.HrSupplierService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class HrSupplierController {
    
    private final HrSupplierService hrSupplierService;
    
    @GetMapping
    public Result<Page<HrSupplier>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String level) {
        Page<HrSupplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrSupplier> wrapper = new LambdaQueryWrapper<>();
        if (supplierName != null) {
            wrapper.like(HrSupplier::getSupplierName, supplierName);
        }
        if (level != null) {
            wrapper.eq(HrSupplier::getLevel, level);
        }
        wrapper.orderByDesc(HrSupplier::getCreateTime);
        return Result.success(hrSupplierService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrSupplier> getById(@PathVariable Long id) {
        return Result.success(hrSupplierService.getById(id));
    }
    
    @PostMapping
    @OperationLog(module = "供应商管理", action = "新增供应商", targetType = "供应商")
    public Result<Void> save(@RequestBody HrSupplier hrSupplier) {
        hrSupplierService.save(hrSupplier);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @OperationLog(module = "供应商管理", action = "编辑供应商", targetType = "供应商")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrSupplier hrSupplier) {
        hrSupplier.setId(id);
        hrSupplierService.updateById(hrSupplier);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @OperationLog(module = "供应商管理", action = "删除供应商", targetType = "供应商")
    public Result<Void> delete(@PathVariable Long id) {
        hrSupplierService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(hrSupplierService.list());
    }
}
