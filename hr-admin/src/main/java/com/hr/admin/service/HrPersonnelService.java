package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.HrPersonnel;

public interface HrPersonnelService extends IService<HrPersonnel> {
    void savePersonnel(HrPersonnel hrPersonnel);
    void entry(Long id);
    void exit(Long id);
    void reEntry(Long id);
    void transfer(Long id, Long targetProjectId);
}
