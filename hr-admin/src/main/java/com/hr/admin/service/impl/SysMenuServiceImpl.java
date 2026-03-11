package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.SysMenu;
import com.hr.admin.entity.SysRoleMenu;
import com.hr.admin.mapper.SysMenuMapper;
import com.hr.admin.mapper.SysRoleMenuMapper;
import com.hr.admin.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    
    private final SysRoleMenuMapper sysRoleMenuMapper;
    
    @Override
    public List<SysMenu> getMenusByRole(String role) {
        LambdaQueryWrapper<SysRoleMenu> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.eq(SysRoleMenu::getRole, role);
        List<Long> menuIds = sysRoleMenuMapper.selectList(rmWrapper).stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
        
        if (menuIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getId, menuIds)
               .eq(SysMenu::getVisible, 1)
               .orderByAsc(SysMenu::getSortOrder);
        return list(wrapper);
    }
}
