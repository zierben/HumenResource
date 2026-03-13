package com.hr.admin.service.impl;

import com.hr.admin.dto.LoginDTO;
import com.hr.admin.dto.LoginVO;
import com.hr.admin.entity.SysUser;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SysUserService sysUserService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private SysUser testUser;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRealName("测试用户");
        testUser.setRole("HR");
        testUser.setStatus(1);

        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
    }

    @Test
    @DisplayName("登录成功")
    void testLogin_Success() {
        when(sysUserService.getByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "HR")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refreshToken");

        LoginVO result = authService.login(loginDTO);

        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
        assertEquals("HR", result.getRole());

        verify(sysUserService).getByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil).generateToken("testuser", "HR");
        verify(jwtUtil).generateRefreshToken("testuser");
    }

    @Test
    @DisplayName("登录失败-用户不存在")
    void testLogin_UserNotFound() {
        when(sysUserService.getByUsername("testuser")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("用户名或密码错误", exception.getMessage());
        verify(sysUserService).getByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("登录失败-账号被禁用")
    void testLogin_AccountDisabled() {
        testUser.setStatus(0);
        when(sysUserService.getByUsername("testuser")).thenReturn(testUser);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("账号已被禁用", exception.getMessage());
        verify(sysUserService).getByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("登录失败-密码错误")
    void testLogin_WrongPassword() {
        when(sysUserService.getByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("用户名或密码错误", exception.getMessage());
        verify(sysUserService).getByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("获取当前用户")
    void testGetCurrentUser() {
        when(sysUserService.getByUsername("testuser")).thenReturn(testUser);

        SysUser result = authService.getCurrentUser("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(sysUserService).getByUsername("testuser");
    }

    @Test
    @DisplayName("获取当前用户-用户不存在")
    void testGetCurrentUser_NotFound() {
        when(sysUserService.getByUsername("nonexistent")).thenReturn(null);

        SysUser result = authService.getCurrentUser("nonexistent");

        assertNull(result);
        verify(sysUserService).getByUsername("nonexistent");
    }
}
