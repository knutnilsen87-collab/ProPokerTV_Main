package com.propokertv.api.support;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "DB_HOST=localhost",
        "DB_PORT=55436",
        "DB_NAME=propokertv",
        "DB_USERNAME=propokertv",
        "DB_PASSWORD=propokertv",
        "JWT_SECRET=test-secret-key-that-is-definitely-long-enough-1234567890",
        "JWT_ISSUER=propokertv-test",
        "JWT_ACCESS_TOKEN_TTL_MINUTES=15",
        "JWT_REFRESH_TOKEN_TTL_DAYS=30",
        "JWT_PASSWORD_RESET_TOKEN_TTL_MINUTES=30",
        "JWT_EMAIL_VERIFICATION_TOKEN_TTL_HOURS=24",
        "CORS_ALLOWED_ORIGINS=http://localhost:3000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {}
