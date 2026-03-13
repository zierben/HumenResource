package com.hr.admin.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    public void addToBlacklist(String token) {
        blacklistedTokens.put(token, System.currentTimeMillis());
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    public void cleanup() {
        long now = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> 
            now - entry.getValue() > 24 * 60 * 60 * 1000
        );
    }
}
