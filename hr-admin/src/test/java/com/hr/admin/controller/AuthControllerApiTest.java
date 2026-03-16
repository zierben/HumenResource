package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest extends BaseApiTest {

    @Test
    @Order(1)
    void testLoginSuccess() {
        String url = getBaseUrl() + "/api/auth/login";
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        
        HttpEntity<String> entity = new HttpEntity<>(body, getAdminHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"code\":200"));
        assertTrue(response.getBody().contains("\"token\""));
    }

    @Test
    @Order(2)
    void testLoginWrongPassword() {
        String url = getBaseUrl() + "/api/auth/login";
        String body = "{\"username\":\"admin\",\"password\":\"wrongpassword\"}";
        
        HttpEntity<String> entity = new HttpEntity<>(body, getAdminHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"code\":") && response.getBody().contains("错误"));
    }

    @Test
    @Order(3)
    void testLoginUserNotFound() {
        String url = getBaseUrl() + "/api/auth/login";
        String body = "{\"username\":\"nonexistent\",\"password\":\"admin123\"}";
        
        HttpEntity<String> entity = new HttpEntity<>(body, getAdminHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("错误"));
    }

    @Test
    @Order(4)
    void testGetCurrentUserWithToken() {
        loginAsAdmin();
        
        String url = getBaseUrl() + "/api/auth/current";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"code\":200"));
    }

    @Test
    @Order(5)
    void testLogout() {
        loginAsAdmin();
        
        String url = getBaseUrl() + "/api/auth/logout";
        HttpHeaders headers = getAdminHeaders();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        
        assertNotNull(response.getBody());
    }

    @Test
    @Order(6)
    void testInitAdmin() {
        String url = getBaseUrl() + "/api/auth/init-admin";
        String body = "{}";
        
        HttpEntity<String> entity = new HttpEntity<>(body, getAdminHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
