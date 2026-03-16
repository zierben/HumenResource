package com.hr.admin.aspect;

import com.hr.admin.annotation.RateLimit;
import com.hr.admin.dto.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RateLimitInfo> rateLimitCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${rate-limit.enabled:false}")
    private boolean enabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!enabled) {
            return true;
        }

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true;
        }

        String key = getRateLimitKey(request, rateLimit);
        RateLimitInfo info = rateLimitCache.computeIfAbsent(key, k -> new RateLimitInfo(rateLimit.value(), rateLimit.timeWindow()));

        if (info.isBlocked()) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\"}");
            return false;
        }

        return true;
    }

    private String getRateLimitKey(HttpServletRequest request, RateLimit rateLimit) {
        String baseKey = rateLimit.key();
        if (baseKey == null || baseKey.isEmpty()) {
            String ip = getClientIp(request);
            String path = request.getRequestURI();
            return ip + ":" + path;
        }
        return baseKey;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private static class RateLimitInfo {
        private final int maxRequests;
        private final int timeWindowSeconds;
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        public RateLimitInfo(int maxRequests, int timeWindowSeconds) {
            this.maxRequests = maxRequests;
            this.timeWindowSeconds = timeWindowSeconds;
        }

        public boolean isBlocked() {
            long now = System.currentTimeMillis();
            if (now - windowStart > timeWindowSeconds * 1000L) {
                windowStart = now;
                count.set(0);
            }
            return count.incrementAndGet() > maxRequests;
        }
    }
}
