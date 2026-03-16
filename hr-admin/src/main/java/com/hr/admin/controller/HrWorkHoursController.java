package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrWorkHours;
import com.hr.admin.service.HrPersonnelService;
import com.hr.admin.service.HrWorkHoursService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/work-hours")
@RequiredArgsConstructor
public class HrWorkHoursController {
    
    private final HrWorkHoursService hrWorkHoursService;
    private final HrPersonnelService hrPersonnelService;
    
    @GetMapping
    public Result<Page<HrWorkHours>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long personnelId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Page<HrWorkHours> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrWorkHours> wrapper = new LambdaQueryWrapper<>();
        if (personnelId != null) {
            wrapper.eq(HrWorkHours::getPersonnelId, personnelId);
        }
        if (projectId != null) {
            wrapper.eq(HrWorkHours::getProjectId, projectId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(HrWorkHours::getStatus, status);
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
        if (hrWorkHoursService.existsDuplicate(
                hrWorkHours.getPersonnelId(), 
                hrWorkHours.getProjectId(), 
                hrWorkHours.getWorkDate(), 
                null)) {
            return Result.error("该人员当日已有该项目工时记录，不能重复添加");
        }
        hrWorkHoursService.save(hrWorkHours);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrWorkHours hrWorkHours) {
        hrWorkHours.setId(id);
        if (hrWorkHoursService.existsDuplicate(
                hrWorkHours.getPersonnelId(), 
                hrWorkHours.getProjectId(), 
                hrWorkHours.getWorkDate(), 
                id)) {
            return Result.error("该人员当日已有该项目工时记录，不能重复添加");
        }
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
    
    @GetMapping("/my-attendance")
    public Result<Map<String, Object>> getMyAttendance(@RequestParam String username) {
        LambdaQueryWrapper<HrPersonnel> personnelWrapper = new LambdaQueryWrapper<>();
        personnelWrapper.eq(HrPersonnel::getZentaoAccount, username);
        HrPersonnel personnel = hrPersonnelService.getOne(personnelWrapper);
        
        if (personnel == null) {
            return Result.error("未找到关联的人员信息");
        }
        
        Long personnelId = personnel.getId();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        
        LambdaQueryWrapper<HrWorkHours> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrWorkHours::getPersonnelId, personnelId);
        wrapper.ge(HrWorkHours::getWorkDate, startOfMonth);
        wrapper.le(HrWorkHours::getWorkDate, now);
        
        List<HrWorkHours> workHoursList = hrWorkHoursService.list(wrapper);
        
        int leaveCount = 0;
        int absentCount = 0;
        int normalCount = 0;
        List<Map<String, Object>> leaveList = new ArrayList<>();
        List<Map<String, Object>> absentList = new ArrayList<>();
        
        for (HrWorkHours wh : workHoursList) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", wh.getWorkDate());
            item.put("hours", wh.getHours());
            item.put("remark", wh.getRemark());
            item.put("status", wh.getStatus());
            
            if ("LEAVE".equals(wh.getStatus())) {
                leaveCount++;
                leaveList.add(item);
            } else if (wh.getHours() != null && wh.getHours().compareTo(java.math.BigDecimal.ZERO) == 0 && !"LEAVE".equals(wh.getStatus())) {
                absentCount++;
                absentList.add(item);
            } else if ("APPROVED".equals(wh.getStatus())) {
                normalCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("personnelId", personnelId);
        result.put("personnelName", personnel.getName());
        result.put("leaveDays", leaveCount);
        result.put("absentDays", absentCount);
        result.put("normalDays", normalCount);
        result.put("totalDays", workHoursList.size());
        result.put("leaveList", leaveList);
        result.put("absentList", absentList);
        
        return Result.success(result);
    }
    
    @GetMapping("/personnel/{personnelId}/attendance")
    public Result<Map<String, Object>> getPersonnelAttendance(@PathVariable Long personnelId) {
        HrPersonnel personnel = hrPersonnelService.getById(personnelId);
        
        if (personnel == null) {
            return Result.error("未找到该人员信息");
        }
        
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        
        LambdaQueryWrapper<HrWorkHours> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrWorkHours::getPersonnelId, personnelId);
        wrapper.ge(HrWorkHours::getWorkDate, startOfMonth);
        wrapper.le(HrWorkHours::getWorkDate, now);
        
        List<HrWorkHours> workHoursList = hrWorkHoursService.list(wrapper);
        
        int leaveCount = 0;
        int absentCount = 0;
        int normalCount = 0;
        List<Map<String, Object>> leaveList = new ArrayList<>();
        List<Map<String, Object>> absentList = new ArrayList<>();
        
        for (HrWorkHours wh : workHoursList) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", wh.getWorkDate());
            item.put("hours", wh.getHours());
            item.put("remark", wh.getRemark());
            item.put("status", wh.getStatus());
            
            if ("LEAVE".equals(wh.getStatus())) {
                leaveCount++;
                leaveList.add(item);
            } else if (wh.getHours() != null && wh.getHours().compareTo(java.math.BigDecimal.ZERO) == 0 && !"LEAVE".equals(wh.getStatus())) {
                absentCount++;
                absentList.add(item);
            } else if ("APPROVED".equals(wh.getStatus())) {
                normalCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("personnelId", personnelId);
        result.put("personnelName", personnel.getName());
        result.put("leaveDays", leaveCount);
        result.put("absentDays", absentCount);
        result.put("normalDays", normalCount);
        result.put("totalDays", workHoursList.size());
        result.put("leaveList", leaveList);
        result.put("absentList", absentList);
        
        return Result.success(result);
    }
}
