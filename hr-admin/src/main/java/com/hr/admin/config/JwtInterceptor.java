package com.hr.admin.config;

import com.hr.admin.util.JwtUtil;
import com.hr.admin.dto.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        log.info("JwtInterceptor preHandle: uri={}, method={}", uri, request.getMethod());
        
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        if ("/api/auth/login".equals(uri) || "/api/auth/logout".equals(uri) || "/api/auth/reset-admin".equals(uri) || "/api/auth/reset-all".equals(uri) || "/api/auth/init-admin".equals(uri)) {
            log.info("Skipping auth for excluded path: {}", uri);
            return true;
        }
        
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendError(response, "未登录");
            return false;
        }
        
        String jwt = token.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            sendError(response, "Token无效或已过期");
            return false;
        }
        
        String username = jwtUtil.getUsername(jwt);
        String role = jwtUtil.getRole(jwt);
        request.setAttribute("currentUser", username);
        request.setAttribute("currentRole", role);
        return true;
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        Result<Void> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
