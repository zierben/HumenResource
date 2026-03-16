package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HrSettlementControllerTest extends BaseApiTest {

    @BeforeAll
    void setup() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testListSettlements() {
        String url = getBaseUrl() + "/api/settlements?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testGetSettlementById() {
        String url = getBaseUrl() + "/api/settlements/1";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
