package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.service.HrPersonnelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class HrPersonnelController {
    
    private final HrPersonnelService hrPersonnelService;
    
    @GetMapping
    public Result<Page<HrPersonnel>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long projectId) {
        Page<HrPersonnel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrPersonnel> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(HrPersonnel::getName, name);
        }
        if (status != null) {
            wrapper.eq(HrPersonnel::getStatus, status);
        }
        if (projectId != null) {
            wrapper.eq(HrPersonnel::getProjectId, projectId);
        }
        wrapper.orderByDesc(HrPersonnel::getCreateTime);
        return Result.success(hrPersonnelService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrPersonnel> getById(@PathVariable Long id) {
        return Result.success(hrPersonnelService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrPersonnel hrPersonnel) {
        hrPersonnelService.savePersonnel(hrPersonnel);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrPersonnel hrPersonnel) {
        hrPersonnel.setId(id);
        hrPersonnelService.updateById(hrPersonnel);
        return Result.success();
    }
    
    @PostMapping("/{id}/entry")
    public Result<Void> entry(@PathVariable Long id) {
        hrPersonnelService.entry(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/exit")
    public Result<Void> exit(@PathVariable Long id) {
        hrPersonnelService.exit(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/transfer")
    public Result<Void> transfer(@PathVariable Long id, @RequestParam Long targetProjectId) {
        hrPersonnelService.transfer(id, targetProjectId);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrPersonnelService.removeById(id);
        return Result.success();
    }
}
