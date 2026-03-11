package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrRequirement;
import com.hr.admin.service.HrRequirementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
public class HrRequirementController {
    
    private final HrRequirementService hrRequirementService;
    
    @GetMapping
    public Result<Page<HrRequirement>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) Integer status) {
        Page<HrRequirement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrRequirement> wrapper = new LambdaQueryWrapper<>();
        if (projectName != null) {
            wrapper.like(HrRequirement::getProjectName, projectName);
        }
        if (status != null) {
            wrapper.eq(HrRequirement::getStatus, status);
        }
        wrapper.orderByDesc(HrRequirement::getCreateTime);
        return Result.success(hrRequirementService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrRequirement> getById(@PathVariable Long id) {
        return Result.success(hrRequirementService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrRequirement hrRequirement) {
        hrRequirementService.saveRequirement(hrRequirement);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrRequirement hrRequirement) {
        hrRequirement.setId(id);
        hrRequirementService.updateRequirement(hrRequirement);
        return Result.success();
    }
    
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestParam Integer status, 
                                @RequestParam(required = false) String rejectReason) {
        hrRequirementService.approve(id, status, rejectReason);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrRequirementService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/stats")
    public Result<List<Object>> getStats() {
        return Result.success(hrRequirementService.getStats());
    }
}
