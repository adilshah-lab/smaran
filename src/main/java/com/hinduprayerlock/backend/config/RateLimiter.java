package com.hinduprayerlock.backend.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();

    public boolean allowRequest(String key) {

        requestCounts.putIfAbsent(key, 0);

        if (requestCounts.get(key) > 10) {
            return false;
        }

        requestCounts.put(key, requestCounts.get(key) + 1);

        return true;
    }
}
