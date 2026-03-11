package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.HrRequirement;
import java.util.List;

public interface HrRequirementService extends IService<HrRequirement> {
    void saveRequirement(HrRequirement hrRequirement);
    void updateRequirement(HrRequirement hrRequirement);
    void approve(Long id, Integer status, String rejectReason);
    List<Object> getStats();
}
