package com.hr.admin.controller;

import com.hr.admin.annotation.OperationLog;
import com.hr.admin.dto.LoginDTO;
import com.hr.admin.dto.LoginVO;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.SysUser;
import com.hr.admin.security.LoginAttemptService;
import com.hr.admin.security.TokenBlacklistService;
import com.hr.admin.service.AuthService;
import com.hr.admin.service.SysUserService;
import com.hr.admin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklistService tokenBlacklistService;
    
    @PostMapping("/login")
    @OperationLog(module = "系统管理", action = "登录", targetType = "用户")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String username = loginDTO.getUsername();
        
        if (loginAttemptService.isLocked(username)) {
            long remaining = loginAttemptService.getLockTimeRemaining(username);
            return Result.error("账户已锁定，请" + remaining + "分钟后再试");
        }
        
        try {
            LoginVO result = authService.login(loginDTO);
            loginAttemptService.loginSucceeded(username);
            return Result.success(result);
        } catch (RuntimeException e) {
            loginAttemptService.loginFailed(username);
            int remaining = loginAttemptService.getRemainingAttempts(username);
            if (remaining <= 3 && remaining > 0) {
                return Result.error("用户名或密码错误，剩余" + remaining + "次尝试机会");
            }
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @OperationLog(module = "系统管理", action = "登出", targetType = "用户")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.addToBlacklist(token);
        }
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
    @PreAuthorize("hasRole('ADMIN')")
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
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setLevel(0);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            sysUserService.save(admin);
        } else {
            existing.setPassword(encoded);
            existing.setRole("ADMIN");
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
