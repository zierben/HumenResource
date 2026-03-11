package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrProject;
import com.hr.admin.service.HrProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class HrProjectController {
    
    private final HrProjectService hrProjectService;
    
    @GetMapping
    public Result<Page<HrProject>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String department) {
        Page<HrProject> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrProject> wrapper = new LambdaQueryWrapper<>();
        if (projectName != null) {
            wrapper.like(HrProject::getProjectName, projectName);
        }
        if (department != null) {
            wrapper.eq(HrProject::getDepartment, department);
        }
        wrapper.orderByDesc(HrProject::getCreateTime);
        return Result.success(hrProjectService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrProject> getById(@PathVariable Long id) {
        return Result.success(hrProjectService.getById(id));
    }
    
    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(hrProjectService.list());
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrProject hrProject) {
        hrProjectService.save(hrProject);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrProject hrProject) {
        hrProject.setId(id);
        hrProjectService.updateById(hrProject);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrProjectService.removeById(id);
        return Result.success();
    }
}
