package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HrPersonnelControllerTest extends BaseApiTest {

    @BeforeAll
    void setup() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testListPersonnel() {
        String url = getBaseUrl() + "/api/personnel?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testGetPersonnelById() {
        String url = getBaseUrl() + "/api/personnel/1";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testDeletePersonnel() {
        String url = getBaseUrl() + "/api/personnel/999";
        ResponseEntity<String> response = delete(url, getAdminHeaders(), String.class);
        assertNotNull(response.getBody());
    }

    @Test
    @Order(4)
    void testExportPersonnel() {
        String url = getBaseUrl() + "/api/personnel/export";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
