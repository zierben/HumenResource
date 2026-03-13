package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrDepartment;
import com.hr.admin.entity.SysUser;
import com.hr.admin.service.HrDepartmentService;
import com.hr.admin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class HrDepartmentController {
    
    private final HrDepartmentService hrDepartmentService;
    private final SysUserService sysUserService;
    
    @GetMapping("/tree")
    public Result<List<HrDepartment>> getTree() {
        return Result.success(hrDepartmentService.getTree());
    }
    
    @GetMapping
    public Result<List<HrDepartment>> list() {
        return Result.success(hrDepartmentService.list(
                new LambdaQueryWrapper<HrDepartment>()
                        .orderByAsc(HrDepartment::getLevel)
                        .orderByAsc(HrDepartment::getSortOrder)));
    }
    
    @GetMapping("/{id}")
    public Result<HrDepartment> getById(@PathVariable Long id) {
        return Result.success(hrDepartmentService.getWithDetails(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrDepartment department) {
        hrDepartmentService.save(department);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrDepartment department) {
        department.setId(id);
        hrDepartmentService.updateById(department);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrDepartmentService.removeById(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/manager")
    public Result<Void> setManager(@PathVariable Long id, @RequestParam Long userId) {
        hrDepartmentService.setManager(id, userId);
        return Result.success();
    }
    
    @DeleteMapping("/{id}/manager")
    public Result<Void> removeManager(@PathVariable Long id) {
        hrDepartmentService.removeManager(id);
        return Result.success();
    }
    
    @GetMapping("/{id}/members")
    public Result<List<SysUser>> getMembers(@PathVariable Long id) {
        return Result.success(sysUserService.list(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeptId, id)
                        .orderByDesc(SysUser::getIsManager)));
    }
    
    @PostMapping("/{id}/members")
    public Result<Void> addMember(@PathVariable Long id, @RequestParam Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user != null) {
            user.setDeptId(id);
            sysUserService.updateById(user);
        }
        return Result.success();
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    public Result<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user != null && id.equals(user.getDeptId())) {
            user.setDeptId(null);
            user.setIsManager(0);
            sysUserService.updateById(user);
        }
        return Result.success();
    }
    
    @GetMapping("/level/{level}")
    public Result<List<HrDepartment>> getByLevel(@PathVariable Integer level) {
        return Result.success(hrDepartmentService.getByLevel(level));
    }
}
