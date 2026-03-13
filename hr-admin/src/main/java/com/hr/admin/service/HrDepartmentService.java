package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.HrDepartment;
import java.util.List;

public interface HrDepartmentService extends IService<HrDepartment> {
    
    List<HrDepartment> getTree();
    
    List<HrDepartment> getByParentId(Long parentId);
    
    HrDepartment getWithDetails(Long id);
    
    boolean setManager(Long deptId, Long userId);
    
    boolean removeManager(Long deptId);
    
    List<HrDepartment> getByLevel(Integer level);
}
