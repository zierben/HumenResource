package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HrWorkHoursControllerTest extends BaseApiTest {

    @BeforeAll
    void setup() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testListWorkHours() {
        String url = getBaseUrl() + "/api/work-hours?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testGetWorkHoursSummary() {
        String url = getBaseUrl() + "/api/work-hours/summary?startDate=2026-01-01&endDate=2026-12-31";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
