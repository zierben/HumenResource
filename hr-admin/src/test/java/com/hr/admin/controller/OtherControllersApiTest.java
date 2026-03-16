package com.hr.admin.controller;

import com.hr.admin.config.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OtherControllersTest extends BaseApiTest {

    @BeforeAll
    void setup() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testListCandidates() {
        String url = getBaseUrl() + "/api/candidates?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testListContracts() {
        String url = getBaseUrl() + "/api/contracts?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testListMessages() {
        String url = getBaseUrl() + "/api/messages?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testListDepartments() {
        String url = getBaseUrl() + "/api/departments/tree";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testSearch() {
        String url = getBaseUrl() + "/api/search?keyword=test";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(6)
    void testListLogs() {
        String url = getBaseUrl() + "/api/logs?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testListUsers() {
        String url = getBaseUrl() + "/api/users?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testListPayments() {
        String url = getBaseUrl() + "/api/payments?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(9)
    void testListEvaluations() {
        String url = getBaseUrl() + "/api/evaluations?pageNum=1&pageSize=10";
        ResponseEntity<String> response = get(url, getAdminHeaders(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
