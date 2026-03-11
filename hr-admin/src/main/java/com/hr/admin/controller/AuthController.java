package com.hr.admin.controller;

import com.hr.admin.dto.LoginDTO;
import com.hr.admin.dto.LoginVO;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.SysUser;
import com.hr.admin.service.AuthService;
import com.hr.admin.service.SysUserService;
import com.hr.admin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            return Result.success(authService.login(loginDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
    
    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        String jwt = token.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            return Result.error("Token无效或已过期");
        }
        String username = jwtUtil.getUsername(jwt);
        return Result.success(authService.getCurrentUser(username));
    }
    
    @PostMapping("/reset-admin")
    public Result<Void> resetAdminPassword() {
        SysUser user = sysUserService.getByUsername("admin");
        if (user != null) {
            user.setPassword(passwordEncoder.encode("123456"));
            sysUserService.updateById(user);
            return Result.success();
        }
        return Result.error("用户不存在");
    }
}
