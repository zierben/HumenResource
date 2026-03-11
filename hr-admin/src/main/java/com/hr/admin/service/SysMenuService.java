package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.SysMenu;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getMenusByRole(String role);
}
