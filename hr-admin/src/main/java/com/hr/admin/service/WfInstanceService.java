package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.dto.InstanceDetailDTO;
import com.hr.admin.entity.WfInstance;

public interface WfInstanceService extends IService<WfInstance> {
    WfInstance startWorkflow(Long workflowId, String businessType, Long businessId, String title, Long initiatorId);
    void approve(Long instanceId, Long operatorId, String operatorName, String comment);
    void reject(Long instanceId, Long operatorId, String operatorName, String comment);
    Page<WfInstance> getMyPending(Long userId, Integer pageNum, Integer pageSize);
    Page<WfInstance> getMyDone(Long userId, Integer pageNum, Integer pageSize);
    InstanceDetailDTO getDetail(Long instanceId);
}
