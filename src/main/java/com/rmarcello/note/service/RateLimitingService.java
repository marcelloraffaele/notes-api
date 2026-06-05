package com.rmarcello.note.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitingService.class);
    
    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();
    
    // Rate limit: 100 requests per minute per IP
    private static final int REQUESTS_PER_MINUTE = 100;
    
    public boolean allowRequest(String clientIp) {
        Bucket bucket = cache.computeIfAbsent(clientIp, this::createNewBucket);
        
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            LOGGER.warn("Rate limit exceeded for IP: {}", clientIp);
            return false;
        }
    }
    
    private Bucket createNewBucket(String clientIp) {
        LOGGER.debug("Creating new rate limit bucket for IP: {}", clientIp);
        
        Bandwidth limit = Bandwidth.classic(REQUESTS_PER_MINUTE, Refill.intervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}