package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HrRequirementControllerTest extends BaseApiTest {

    @BeforeAll
    void setup() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testListRequirements() {
        String url = getBaseUrl() + "/api/requirements?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testGetRequirementById() {
        String url = getBaseUrl() + "/api/requirements/1";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testDeleteRequirement() {
        String url = getBaseUrl() + "/api/requirements/999";
        ResponseEntity<String> response = delete(url, getAdminHeaders(), String.class);
        assertNotNull(response.getBody());
    }
}
