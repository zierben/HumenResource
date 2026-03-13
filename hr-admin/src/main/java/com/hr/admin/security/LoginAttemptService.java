package com.hr.admin.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTimeCache = new ConcurrentHashMap<>();
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = 30 * 60 * 1000;
    
    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attemptsCache.put(username, attempts + 1);
        
        if (attempts + 1 >= MAX_ATTEMPTS) {
            lockTimeCache.put(username, System.currentTimeMillis());
        }
    }
    
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockTimeCache.remove(username);
    }
    
    public boolean isLocked(String username) {
        Long lockTime = lockTimeCache.get(username);
        if (lockTime == null) {
            return false;
        }
        
        if (System.currentTimeMillis() - lockTime > LOCK_TIME_MS) {
            attemptsCache.remove(username);
            lockTimeCache.remove(username);
            return false;
        }
        
        return true;
    }
    
    public int getRemainingAttempts(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
    
    public long getLockTimeRemaining(String username) {
        Long lockTime = lockTimeCache.get(username);
        if (lockTime == null) {
            return 0;
        }
        long remaining = LOCK_TIME_MS - (System.currentTimeMillis() - lockTime);
        return Math.max(0, remaining / 60000);
    }
}
