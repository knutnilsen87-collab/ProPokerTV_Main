package com.propokertv.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propokertv.api.common.api.ApiErrorBody;
import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private final AppSecurityProperties appSecurityProperties;
    private final ObjectMapper objectMapper;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String key = clientIp(request) + ":" + request.getRequestURI();
        long now = Instant.now().getEpochSecond();
        Bucket bucket = buckets.compute(key, (ignored, current) -> {
            if (current == null || now >= current.resetAt) {
                return new Bucket(1, now + appSecurityProperties.getAuthRateLimitWindowSeconds());
            }
            current.count++;
            return current;
        });

        if (bucket.count > appSecurityProperties.getAuthRateLimitCapacity()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), ApiEnvelope.error(new ApiErrorBody(
                    ErrorCode.BAD_REQUEST.name(),
                    "Too many authentication attempts. Please wait and try again.",
                    null
            )));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class Bucket {
        private int count;
        private final long resetAt;

        private Bucket(int count, long resetAt) {
            this.count = count;
            this.resetAt = resetAt;
        }
    }
}
