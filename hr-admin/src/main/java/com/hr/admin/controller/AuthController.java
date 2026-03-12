package com.hr.admin.controller;

import com.hr.admin.annotation.OperationLog;
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
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    @OperationLog(module = "系统管理", action = "登录", targetType = "用户")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            return Result.success(authService.login(loginDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @OperationLog(module = "系统管理", action = "登出", targetType = "用户")
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
    
    @PostMapping("/reset-all")
    public Result<String> resetAllPasswords() {
        String encoded = passwordEncoder.encode("admin123");
        for (SysUser user : sysUserService.list()) {
            user.setPassword(encoded);
            sysUserService.updateById(user);
        }
        return Result.success("所有用户密码已重置为admin123");
    }
    
    @PostMapping("/init-admin")
    public Result<String> initAdmin() {
        String encoded = passwordEncoder.encode("admin123");
        
        SysUser existing = sysUserService.getByUsername("admin");
        if (existing == null) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(encoded);
            admin.setRealName("系统管理员");
            admin.setRole("GM");
            admin.setStatus(1);
            admin.setLevel(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            sysUserService.save(admin);
        } else {
            existing.setPassword(encoded);
            existing.setRole("GM");
            sysUserService.updateById(existing);
        }
        
        SysUser hrExisting = sysUserService.getByUsername("hr001");
        if (hrExisting != null) {
            hrExisting.setPassword(encoded);
            sysUserService.updateById(hrExisting);
        }
        
        return Result.success("账号初始化完成: admin/admin123, hr001/admin123");
    }
}
