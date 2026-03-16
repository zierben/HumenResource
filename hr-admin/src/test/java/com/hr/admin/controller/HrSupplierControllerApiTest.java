package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HrSupplierControllerTest extends BaseApiTest {

    @BeforeAll
    void setup() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testListSuppliers() {
        String url = getBaseUrl() + "/api/suppliers?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testGetSupplierById() {
        String url = getBaseUrl() + "/api/suppliers/1";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testDeleteSupplier() {
        String url = getBaseUrl() + "/api/suppliers/999";
        ResponseEntity<String> response = delete(url, getAdminHeaders(), String.class);
        assertNotNull(response.getBody());
    }
}
