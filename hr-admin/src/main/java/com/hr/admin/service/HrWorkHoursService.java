package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.HrWorkHours;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HrWorkHoursService extends IService<HrWorkHours> {
    void approve(Long id);
    List<Map<String, Object>> getSummary(LocalDate startDate, LocalDate endDate, Long projectId);
    void syncFromZentao(String date);
}
