package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrDepartment;
import com.hr.admin.entity.SysUser;
import com.hr.admin.mapper.HrDepartmentMapper;
import com.hr.admin.service.HrDepartmentService;
import com.hr.admin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrDepartmentServiceImpl extends ServiceImpl<HrDepartmentMapper, HrDepartment> implements HrDepartmentService {
    
    private final SysUserService sysUserService;
    
    @Override
    public List<HrDepartment> getTree() {
        List<HrDepartment> all = list(new LambdaQueryWrapper<HrDepartment>()
                .eq(HrDepartment::getStatus, 1)
                .orderByAsc(HrDepartment::getLevel)
                .orderByAsc(HrDepartment::getSortOrder));
        
        Map<Long, String> userNames = sysUserService.list().stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
        
        for (HrDepartment dept : all) {
            if (dept.getManagerId() != null) {
                dept.setManagerName(userNames.get(dept.getManagerId()));
            }
            dept.setMemberCount(baseMapper.countMembers(dept.getId()));
        }
        
        Map<Long, List<HrDepartment>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(d -> d.getParentId() == null ? 0L : d.getParentId()));
        
        for (HrDepartment dept : all) {
            dept.setChildren(childrenMap.getOrDefault(dept.getId(), new ArrayList<>()));
        }
        
        return all.stream()
                .filter(d -> d.getParentId() == null)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<HrDepartment> getByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<HrDepartment>()
                .eq(HrDepartment::getParentId, parentId)
                .orderByAsc(HrDepartment::getSortOrder));
    }
    
    @Override
    public HrDepartment getWithDetails(Long id) {
        HrDepartment dept = getById(id);
        if (dept != null) {
            if (dept.getManagerId() != null) {
                SysUser user = sysUserService.getById(dept.getManagerId());
                if (user != null) {
                    dept.setManagerName(user.getRealName());
                }
            }
            dept.setMemberCount(baseMapper.countMembers(id));
        }
        return dept;
    }
    
    @Override
    @Transactional
    public boolean setManager(Long deptId, Long userId) {
        SysUser existingManager = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, deptId)
                .eq(SysUser::getIsManager, 1));
        
        if (existingManager != null && !existingManager.getId().equals(userId)) {
            existingManager.setIsManager(0);
            sysUserService.updateById(existingManager);
        }
        
        SysUser user = sysUserService.getById(userId);
        if (user != null) {
            user.setDeptId(deptId);
            user.setIsManager(1);
            sysUserService.updateById(user);
            
            HrDepartment dept = getById(deptId);
            dept.setManagerId(userId);
            return updateById(dept);
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean removeManager(Long deptId) {
        HrDepartment dept = getById(deptId);
        if (dept != null && dept.getManagerId() != null) {
            SysUser user = sysUserService.getById(dept.getManagerId());
            if (user != null) {
                user.setIsManager(0);
                sysUserService.updateById(user);
            }
            dept.setManagerId(null);
            return updateById(dept);
        }
        return false;
    }
    
    @Override
    public List<HrDepartment> getByLevel(Integer level) {
        return list(new LambdaQueryWrapper<HrDepartment>()
                .eq(HrDepartment::getLevel, level)
                .orderByAsc(HrDepartment::getSortOrder));
    }
}
