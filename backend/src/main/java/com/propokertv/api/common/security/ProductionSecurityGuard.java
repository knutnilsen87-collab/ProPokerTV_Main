package com.propokertv.api.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ProductionSecurityGuard implements ApplicationRunner {
    private static final String LOCAL_SECRET = "local-development-secret-change-me-before-production-1234567890";

    private final AppSecurityProperties appSecurityProperties;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        boolean production = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        if (!production) {
            return;
        }
        if (LOCAL_SECRET.equals(appSecurityProperties.getSecret())) {
            throw new IllegalStateException("Production cannot start with the local development JWT secret.");
        }
        String origins = appSecurityProperties.getCorsAllowedOrigins();
        if (origins == null || origins.isBlank() || origins.contains("localhost") || origins.contains("*")) {
            throw new IllegalStateException("Production CORS origins must be explicit public origins.");
        }
    }
}
