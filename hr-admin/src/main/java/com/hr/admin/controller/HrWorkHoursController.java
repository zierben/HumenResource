package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrWorkHours;
import com.hr.admin.service.HrWorkHoursService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-hours")
@RequiredArgsConstructor
public class HrWorkHoursController {
    
    private final HrWorkHoursService hrWorkHoursService;
    
    @GetMapping
    public Result<Page<HrWorkHours>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long personnelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Page<HrWorkHours> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrWorkHours> wrapper = new LambdaQueryWrapper<>();
        if (personnelId != null) {
            wrapper.eq(HrWorkHours::getPersonnelId, personnelId);
        }
        if (startDate != null) {
            wrapper.ge(HrWorkHours::getWorkDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(HrWorkHours::getWorkDate, endDate);
        }
        wrapper.orderByDesc(HrWorkHours::getWorkDate);
        return Result.success(hrWorkHoursService.page(page, wrapper));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrWorkHours hrWorkHours) {
        hrWorkHoursService.save(hrWorkHours);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrWorkHours hrWorkHours) {
        hrWorkHours.setId(id);
        hrWorkHoursService.updateById(hrWorkHours);
        return Result.success();
    }
    
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        hrWorkHoursService.approve(id);
        return Result.success();
    }
    
    @GetMapping("/summary")
    public Result<List<Map<String, Object>>> getSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        return Result.success(hrWorkHoursService.getSummary(startDate, endDate, projectId));
    }
    
    @GetMapping("/sync-from-zentao")
    public Result<Void> syncFromZentao(@RequestParam String date) {
        hrWorkHoursService.syncFromZentao(date);
        return Result.success();
    }
}
