package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.constant.HrConstants;
import com.hr.admin.entity.HrWorkHours;
import com.hr.admin.mapper.HrWorkHoursMapper;
import com.hr.admin.service.HrWorkHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HrWorkHoursServiceImpl extends ServiceImpl<HrWorkHoursMapper, HrWorkHours> implements HrWorkHoursService {
    
    @Override
    @Transactional
    public void approve(Long id) {
        HrWorkHours workHours = new HrWorkHours();
        workHours.setId(id);
        workHours.setStatus(HrConstants.WorkHoursStatus.APPROVED);
        workHours.setUpdateTime(LocalDateTime.now());
        updateById(workHours);
    }
    
    @Override
    public List<Map<String, Object>> getSummary(LocalDate startDate, LocalDate endDate, Long projectId) {
        LambdaQueryWrapper<HrWorkHours> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(HrWorkHours::getWorkDate, startDate);
        wrapper.le(HrWorkHours::getWorkDate, endDate);
        if (projectId != null) {
            wrapper.eq(HrWorkHours::getProjectId, projectId);
        }
        List<HrWorkHours> list = list(wrapper);
        
        Map<Long, Map<String, Object>> summaryMap = new HashMap<>();
        for (HrWorkHours wh : list) {
            Long personnelId = wh.getPersonnelId();
            if (!summaryMap.containsKey(personnelId)) {
                Map<String, Object> item = new HashMap<>();
                item.put("personnelId", personnelId);
                item.put("totalHours", 0.0);
                item.put("workDays", 0);
                summaryMap.put(personnelId, item);
            }
            Map<String, Object> item = summaryMap.get(personnelId);
            item.put("totalHours", ((Number) item.get("totalHours")).doubleValue() + wh.getHours().doubleValue());
            item.put("workDays", ((Number) item.get("workDays")).intValue() + 1);
        }
        return new ArrayList<>(summaryMap.values());
    }
    
    @Override
    @Transactional
    public void syncFromZentao(String date) {
        throw new UnsupportedOperationException("禅道同步功能尚未实现，请联系管理员配置禅道API");
    }
}
