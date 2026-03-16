package com.hr.admin.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseApiTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String adminToken;
    protected String hrToken;

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected HttpHeaders getAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (adminToken != null) {
            headers.set("Authorization", "Bearer " + adminToken);
        }
        return headers;
    }

    protected HttpHeaders getHrHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (hrToken != null) {
            headers.set("Authorization", "Bearer " + hrToken);
        }
        return headers;
    }

    protected void loginAsAdmin() {
        String url = getBaseUrl() + "/api/auth/login";
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        
        HttpEntity<String> entity = new HttpEntity<>(body, getAdminHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            if (responseBody != null && responseBody.contains("\"token\"")) {
                int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
                int tokenEnd = responseBody.indexOf("\"", tokenStart);
                if (tokenStart > 8 && tokenEnd > tokenStart) {
                    adminToken = responseBody.substring(tokenStart, tokenEnd);
                }
            }
        }
    }

    protected void loginAsHr() {
        String url = getBaseUrl() + "/api/auth/login";
        String body = "{\"username\":\"hr001\",\"password\":\"admin123\"}";
        
        HttpEntity<String> entity = new HttpEntity<>(body, getHrHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            if (responseBody != null && responseBody.contains("\"token\"")) {
                int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
                int tokenEnd = responseBody.indexOf("\"", tokenStart);
                if (tokenStart > 8 && tokenEnd > tokenStart) {
                    hrToken = responseBody.substring(tokenStart, tokenEnd);
                }
            }
        }
    }

    protected <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType);
    }

    protected <T> ResponseEntity<T> post(String url, Object body, HttpHeaders headers, Class<T> responseType) {
        return restTemplate.postForEntity(url, new HttpEntity<>(body, headers), responseType);
    }

    protected <T> ResponseEntity<T> put(String url, Object body, HttpHeaders headers, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers), responseType);
    }

    protected <T> ResponseEntity<T> delete(String url, HttpHeaders headers, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), responseType);
    }
}
