package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.WfRecord;
import java.util.List;

public interface WfRecordService extends IService<WfRecord> {
    List<WfRecord> getByInstanceId(Long instanceId);
}
