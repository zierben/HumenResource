package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrOperationLog;
import com.hr.admin.service.HrOperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class HrOperationLogController {
    
    private final HrOperationLogService hrOperationLogService;
    
    @GetMapping
    public Result<Page<HrOperationLog>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action) {
        Page<HrOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (module != null) {
            wrapper.eq(HrOperationLog::getModule, module);
        }
        if (action != null) {
            wrapper.eq(HrOperationLog::getAction, action);
        }
        wrapper.orderByDesc(HrOperationLog::getCreateTime);
        return Result.success(hrOperationLogService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrOperationLog> getById(@PathVariable Long id) {
        return Result.success(hrOperationLogService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrOperationLog hrOperationLog) {
        hrOperationLogService.save(hrOperationLog);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrOperationLogService.removeById(id);
        return Result.success();
    }
}
