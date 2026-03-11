package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrSyncLog;
import com.hr.admin.service.HrSyncLogService;
import com.hr.admin.integration.zentao.ZentaoSyncService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {
    
    private final HrSyncLogService hrSyncLogService;
    
    @Autowired(required = false)
    private ZentaoSyncService zentaoSyncService;
    
    @GetMapping("/logs")
    public Result<Page<HrSyncLog>> logs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<HrSyncLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrSyncLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(HrSyncLog::getStartedAt);
        return Result.success(hrSyncLogService.page(page, wrapper));
    }
    
    @PostMapping("/execute")
    public Result<Void> execute(@RequestBody java.util.Map<String, String> params) {
        String syncType = params.get("syncType");
        String date = params.getOrDefault("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        if (zentaoSyncService == null) {
            return Result.error("禅道同步功能未启用");
        }
        
        HrSyncLog log = hrSyncLogService.startLog(syncType, "IMPORT");
        
        try {
            switch (syncType) {
                case "ZENTAO_USER":
                    zentaoSyncService.syncUsers(log.getId());
                    break;
                case "ZENTAO_PROJECT":
                    zentaoSyncService.syncProjects(log.getId());
                    break;
                case "ZENTAO_WORKHOURS":
                    zentaoSyncService.syncWorkHours(log.getId(), date);
                    break;
                default:
                    hrSyncLogService.finishLog(log.getId(), 0, 0, "未知的同步类型: " + syncType);
                    return Result.error("未知的同步类型");
            }
        } catch (Exception e) {
            hrSyncLogService.finishLog(log.getId(), 0, 0, e.getMessage());
            return Result.error("同步失败: " + e.getMessage());
        }
        
        return Result.success();
    }
    
    @PostMapping("/retry/{id}")
    public Result<Void> retry(@PathVariable Long id) {
        if (zentaoSyncService == null) {
            return Result.error("禅道同步功能未启用");
        }
        
        HrSyncLog oldLog = hrSyncLogService.getById(id);
        if (oldLog == null) {
            return Result.error("日志不存在");
        }
        
        HrSyncLog log = hrSyncLogService.startLog(oldLog.getSyncType(), oldLog.getSyncDirection());
        
        try {
            switch (oldLog.getSyncType()) {
                case "ZENTAO_USER":
                    zentaoSyncService.syncUsers(log.getId());
                    break;
                case "ZENTAO_PROJECT":
                    zentaoSyncService.syncProjects(log.getId());
                    break;
                case "ZENTAO_WORKHOURS":
                    zentaoSyncService.syncWorkHours(log.getId(), LocalDate.now().toString());
                    break;
                default:
                    hrSyncLogService.finishLog(log.getId(), 0, 0, "未知的同步类型");
            }
        } catch (Exception e) {
            hrSyncLogService.finishLog(log.getId(), 0, 0, e.getMessage());
            return Result.error("重试失败: " + e.getMessage());
        }
        
        return Result.success();
    }
}
