package com.hr.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.constant.HrConstants;
import com.hr.admin.entity.HrRequirement;
import com.hr.admin.mapper.HrRequirementMapper;
import com.hr.admin.service.HrRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HrRequirementServiceImpl extends ServiceImpl<HrRequirementMapper, HrRequirement> implements HrRequirementService {
    
    @Override
    @Transactional
    public void saveRequirement(HrRequirement hrRequirement) {
        String code = "RQ" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
                     + String.format("%03d", (int)(System.currentTimeMillis() % 1000));
        hrRequirement.setRequirementCode(code);
        hrRequirement.setStatus(HrConstants.RequirementStatus.PENDING_DEPT);
        hrRequirement.setCreateTime(LocalDateTime.now());
        hrRequirement.setUpdateTime(LocalDateTime.now());
        save(hrRequirement);
    }
    
    @Override
    @Transactional
    public void updateRequirement(HrRequirement hrRequirement) {
        hrRequirement.setUpdateTime(LocalDateTime.now());
        updateById(hrRequirement);
    }
    
    @Override
    @Transactional
    public void approve(Long id, Integer status, String rejectReason) {
        HrRequirement requirement = new HrRequirement();
        requirement.setId(id);
        requirement.setStatus(status);
        if (StrUtil.isNotEmpty(rejectReason)) {
            requirement.setRejectReason(rejectReason);
        }
        requirement.setUpdateTime(LocalDateTime.now());
        updateById(requirement);
    }
    
    @Override
    public List<Object> getStats() {
        List<Object> stats = new ArrayList<>();
        
        Long totalCount = count();
        LambdaQueryWrapper<HrRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrRequirement::getStatus, HrConstants.RequirementStatus.PENDING_DEPT);
        wrapper.or().eq(HrRequirement::getStatus, HrConstants.RequirementStatus.PENDING_HR);
        wrapper.or().eq(HrRequirement::getStatus, HrConstants.RequirementStatus.PENDING_FINANCE);
        Long pendingCount = count(wrapper);
        
        LambdaQueryWrapper<HrRequirement> approvedWrapper = new LambdaQueryWrapper<>();
        approvedWrapper.eq(HrRequirement::getStatus, HrConstants.RequirementStatus.APPROVED);
        Long approvedCount = count(approvedWrapper);
        
        stats.add(Map.of("total", totalCount, "pending", pendingCount, "approved", approvedCount));
        return stats;
    }
}
