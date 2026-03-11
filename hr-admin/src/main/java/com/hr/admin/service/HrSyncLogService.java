package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.HrSyncLog;

public interface HrSyncLogService extends IService<HrSyncLog> {
    HrSyncLog startLog(String syncType, String syncDirection);
    void finishLog(Long logId, Integer totalCount, Integer successCount, String errorMessage);
}
