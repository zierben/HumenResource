package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrPayment;
import com.hr.admin.service.HrPaymentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class HrPaymentController {
    
    private final HrPaymentService hrPaymentService;
    
    @GetMapping
    public Result<Page<HrPayment>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String paymentCode,
            @RequestParam(required = false) Integer status) {
        Page<HrPayment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrPayment> wrapper = new LambdaQueryWrapper<>();
        if (paymentCode != null) {
            wrapper.like(HrPayment::getPaymentCode, paymentCode);
        }
        if (status != null) {
            wrapper.eq(HrPayment::getStatus, status);
        }
        wrapper.orderByDesc(HrPayment::getCreateTime);
        return Result.success(hrPaymentService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrPayment> getById(@PathVariable Long id) {
        return Result.success(hrPaymentService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrPayment hrPayment) {
        hrPaymentService.save(hrPayment);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrPayment hrPayment) {
        hrPayment.setId(id);
        hrPaymentService.updateById(hrPayment);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrPaymentService.removeById(id);
        return Result.success();
    }
}
