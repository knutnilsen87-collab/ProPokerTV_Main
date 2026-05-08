package com.propokertv.api.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    private String issuer;
    private long accessTokenTtlMinutes;
    private long refreshTokenTtlDays;
    private long passwordResetTokenTtlMinutes;
    private long emailVerificationTokenTtlHours;
    private String secret;
    private String corsAllowedOrigins;
}
