package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.constant.HrConstants;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.mapper.HrPersonnelMapper;
import com.hr.admin.service.HrPersonnelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HrPersonnelServiceImpl extends ServiceImpl<HrPersonnelMapper, HrPersonnel> implements HrPersonnelService {
    
    @Override
    @Transactional
    public void savePersonnel(HrPersonnel hrPersonnel) {
        String code = "PER" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
                      + String.format("%03d", (int)(System.currentTimeMillis() % 1000));
        hrPersonnel.setPersonnelCode(code);
        hrPersonnel.setStatus(HrConstants.PersonnelStatus.PENDING_ENTRY);
        hrPersonnel.setCreateTime(LocalDateTime.now());
        hrPersonnel.setUpdateTime(LocalDateTime.now());
        save(hrPersonnel);
    }
    
    @Override
    @Transactional
    public void entry(Long id) {
        HrPersonnel personnel = new HrPersonnel();
        personnel.setId(id);
        personnel.setStatus(HrConstants.PersonnelStatus.ON_DUTY);
        personnel.setEntryDate(java.time.LocalDate.now());
        personnel.setUpdateTime(LocalDateTime.now());
        updateById(personnel);
    }
    
    @Override
    @Transactional
    public void exit(Long id) {
        HrPersonnel personnel = new HrPersonnel();
        personnel.setId(id);
        personnel.setStatus(HrConstants.PersonnelStatus.OFF_DUTY);
        personnel.setUpdateTime(LocalDateTime.now());
        updateById(personnel);
    }
    
    @Override
    @Transactional
    public void transfer(Long id, Long targetProjectId) {
        HrPersonnel personnel = new HrPersonnel();
        personnel.setId(id);
        personnel.setProjectId(targetProjectId);
        personnel.setStatus(HrConstants.PersonnelStatus.TRANSFER);
        personnel.setUpdateTime(LocalDateTime.now());
        updateById(personnel);
    }
}
