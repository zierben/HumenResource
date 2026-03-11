package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrSyncLog;
import com.hr.admin.mapper.HrSyncLogMapper;
import com.hr.admin.service.HrSyncLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class HrSyncLogServiceImpl extends ServiceImpl<HrSyncLogMapper, HrSyncLog> implements HrSyncLogService {
    
    @Override
    public HrSyncLog startLog(String syncType, String syncDirection) {
        HrSyncLog log = new HrSyncLog();
        log.setSyncType(syncType);
        log.setSyncDirection(syncDirection);
        log.setStatus("RUNNING");
        log.setStartedAt(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        save(log);
        return log;
    }
    
    @Override
    public void finishLog(Long logId, Integer totalCount, Integer successCount, String errorMessage) {
        HrSyncLog log = new HrSyncLog();
        log.setId(logId);
        log.setStatus(errorMessage == null ? "SUCCESS" : "FAILED");
        log.setTotalCount(totalCount);
        log.setSuccessCount(successCount);
        log.setErrorMessage(errorMessage);
        log.setFinishedAt(LocalDateTime.now());
        updateById(log);
    }
}
