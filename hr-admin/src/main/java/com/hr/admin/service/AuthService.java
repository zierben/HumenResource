package com.hr.admin.service;

import com.hr.admin.dto.LoginDTO;
import com.hr.admin.dto.LoginVO;
import com.hr.admin.entity.SysUser;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);
    SysUser getCurrentUser(String username);
}
