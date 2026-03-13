package com.hr.admin.controller;

import com.hr.admin.dto.LoginDTO;
import com.hr.admin.dto.LoginVO;
import com.hr.admin.entity.SysUser;
import com.hr.admin.security.LoginAttemptService;
import com.hr.admin.security.TokenBlacklistService;
import com.hr.admin.service.AuthService;
import com.hr.admin.service.SysUserService;
import com.hr.admin.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SysUserService sysUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthController authController;

    private LoginDTO loginDTO;
    private LoginVO loginVO;
    private SysUser testUser;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        loginVO = new LoginVO();
        loginVO.setToken("accessToken");
        loginVO.setRefreshToken("refreshToken");
        loginVO.setUsername("testuser");
        loginVO.setRealName("测试用户");
        loginVO.setRole("HR");

        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRealName("测试用户");
        testUser.setRole("HR");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("登录成功")
    void testLogin_Success() {
        when(loginAttemptService.isLocked("testuser")).thenReturn(false);
        when(authService.login(loginDTO)).thenReturn(loginVO);

        var result = authController.login(loginDTO, null);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("accessToken", result.getData().getToken());
        
        verify(loginAttemptService).loginSucceeded("testuser");
    }

    @Test
    @DisplayName("登录失败-账户锁定")
    void testLogin_AccountLocked() {
        when(loginAttemptService.isLocked("testuser")).thenReturn(true);
        when(loginAttemptService.getLockTimeRemaining("testuser")).thenReturn(30L);

        var result = authController.login(loginDTO, null);

        assertNotNull(result);
        assertTrue(result.getMessage().contains("账户已锁定"));
        
        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("登录失败-密码错误")
    void testLogin_WrongPassword() {
        when(loginAttemptService.isLocked("testuser")).thenReturn(false);
        when(authService.login(loginDTO)).thenThrow(new RuntimeException("用户名或密码错误"));
        when(loginAttemptService.getRemainingAttempts("testuser")).thenReturn(2);

        var result = authController.login(loginDTO, null);

        assertNotNull(result);
        assertTrue(result.getMessage().contains("剩余"));
        
        verify(loginAttemptService).loginFailed("testuser");
    }

    @Test
    @DisplayName("登出-添加Token到黑名单")
    void testLogout() {
        String authHeader = "Bearer testToken";

        var result = authController.logout(authHeader);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        
        verify(tokenBlacklistService).addToBlacklist("testToken");
    }

    @Test
    @DisplayName("登出-无Token")
    void testLogout_NoToken() {
        var result = authController.logout(null);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        
        verify(tokenBlacklistService, never()).addToBlacklist(anyString());
    }

    @Test
    @DisplayName("获取当前用户-成功")
    void testGetCurrentUser_Success() {
        String authHeader = "Bearer validToken";
        
        when(jwtUtil.validateToken("validToken")).thenReturn(true);
        when(jwtUtil.getUsername("validToken")).thenReturn("testuser");
        when(authService.getCurrentUser("testuser")).thenReturn(testUser);

        var result = authController.getCurrentUser(authHeader);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("testuser", result.getData().getUsername());
    }

    @Test
    @DisplayName("获取当前用户-无Token")
    void testGetCurrentUser_NoToken() {
        var result = authController.getCurrentUser(null);

        assertNotNull(result);
        assertTrue(result.getMessage().contains("未登录"));
    }

    @Test
    @DisplayName("获取当前用户-Token无效")
    void testGetCurrentUser_InvalidToken() {
        String authHeader = "Bearer invalidToken";
        
        when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

        var result = authController.getCurrentUser(authHeader);

        assertNotNull(result);
        assertTrue(result.getMessage().contains("Token无效"));
    }

    @Test
    @DisplayName("初始化管理员-创建新管理员")
    void testInitAdmin_CreateNew() {
        when(sysUserService.getByUsername("admin")).thenReturn(null);
        when(sysUserService.getByUsername("hr001")).thenReturn(null);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");
        when(sysUserService.save(any(SysUser.class))).thenReturn(true);

        var result = authController.initAdmin();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getMessage());
        
        verify(sysUserService).save(any(SysUser.class));
    }

    @Test
    @DisplayName("初始化管理员-更新现有管理员")
    void testInitAdmin_UpdateExisting() {
        SysUser existingAdmin = new SysUser();
        existingAdmin.setId(1L);
        existingAdmin.setUsername("admin");
        
        when(sysUserService.getByUsername("admin")).thenReturn(existingAdmin);
        when(sysUserService.getByUsername("hr001")).thenReturn(null);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");
        when(sysUserService.updateById(any(SysUser.class))).thenReturn(true);

        var result = authController.initAdmin();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getMessage());
        
        verify(sysUserService).updateById(any(SysUser.class));
    }
}
