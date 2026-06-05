package com.rmarcello.note.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitingFilter.class);
    
    // Store buckets per IP address
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Rate limit: 100 requests per minute per IP
    private static final int REQUESTS_PER_MINUTE = 100;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIp = getClientIp(httpRequest);
        Bucket bucket = getBucketForIp(clientIp);

        if (bucket.tryConsume(1)) {
            // Request allowed
            chain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            LOGGER.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, httpRequest.getRequestURI());
            httpResponse.setStatus(429); // 429 Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    private Bucket getBucketForIp(String ip) {
        return buckets.computeIfAbsent(ip, this::createNewBucket);
    }

    private Bucket createNewBucket(String ip) {
        // Create a bucket with 100 requests per minute capacity
        Bandwidth limit = Bandwidth.classic(REQUESTS_PER_MINUTE, Refill.intervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}