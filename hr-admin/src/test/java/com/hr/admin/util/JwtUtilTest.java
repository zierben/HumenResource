package com.hr.admin.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "TestSecretKeyMustBeAtLeast256BitsLongForSecurity123456789";
    private final Long testExpiration = 3600000L;
    private final Long testRefreshExpiration = 86400000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", testRefreshExpiration);
    }

    @Test
    @DisplayName("生成访问Token")
    void testGenerateToken() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("testuser", claims.getSubject());
        assertEquals("testuser", claims.get("username", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("access", claims.get("type", String.class));
    }

    @Test
    @DisplayName("生成刷新Token")
    void testGenerateRefreshToken() {
        String refreshToken = jwtUtil.generateRefreshToken("testuser");
        
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
        
        Claims claims = jwtUtil.parseToken(refreshToken);
        assertEquals("testuser", claims.getSubject());
        assertEquals("refresh", claims.get("type", String.class));
    }

    @Test
    @DisplayName("从Token获取用户名")
    void testGetUsername() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        String username = jwtUtil.getUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("从Token获取角色")
    void testGetRole() {
        String token = jwtUtil.generateToken("testuser", "HR");
        String role = jwtUtil.getRole(token);
        
        assertEquals("HR", role);
    }

    @Test
    @DisplayName("判断是否为刷新Token")
    void testIsRefreshToken() {
        String accessToken = jwtUtil.generateToken("testuser", "ADMIN");
        String refreshToken = jwtUtil.generateRefreshToken("testuser");
        
        assertFalse(jwtUtil.isRefreshToken(accessToken));
        assertTrue(jwtUtil.isRefreshToken(refreshToken));
    }

    @Test
    @DisplayName("验证有效Token")
    void testValidateToken_Valid() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isExpired(token));
    }

    @Test
    @DisplayName("验证无效Token")
    void testValidateToken_Invalid() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(jwtUtil.validateToken(invalidToken));
        assertTrue(jwtUtil.isExpired(invalidToken));
    }

    @Test
    @DisplayName("获取Token过期时间")
    void testGetExpirationTime() {
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        long expirationTime = jwtUtil.getExpirationTime(token);
        
        assertTrue(expirationTime > beforeGeneration);
        assertTrue(expirationTime <= beforeGeneration + testExpiration + 1000);
    }

    @Test
    @DisplayName("不同用户生成不同Token")
    void testDifferentUsers_DifferentTokens() {
        String token1 = jwtUtil.generateToken("user1", "ADMIN");
        String token2 = jwtUtil.generateToken("user2", "ADMIN");
        
        assertNotEquals(token1, token2);
        assertEquals("user1", jwtUtil.getUsername(token1));
        assertEquals("user2", jwtUtil.getUsername(token2));
    }

    @Test
    @DisplayName("Token包含正确的用户信息")
    void testTokenContainsCorrectUserInfo() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("testuser", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("access", claims.get("type", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}
