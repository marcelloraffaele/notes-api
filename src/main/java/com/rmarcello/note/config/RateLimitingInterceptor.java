package com.rmarcello.note.config;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    // Allow 100 requests per minute per IP (roughly 1.67 requests per second)
    private RateLimiter createRateLimiter() {
        return RateLimiter.create(1.67); // 100 requests per 60 seconds
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIP(request);
        RateLimiter rateLimiter = rateLimiters.computeIfAbsent(clientIp, k -> createRateLimiter());

        if (rateLimiter.tryAcquire()) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            response.getWriter().write("Too many requests - rate limit exceeded");
            return false;
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}