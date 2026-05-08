package com.propokertv.api.auth.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.social-auth")
public class SocialAuthProperties {
    private Map<String, Provider> providers = new HashMap<>();

    @Getter
    @Setter
    public static class Provider {
        private String issuer;
        private String jwkSetUri;
        private String clientId;
        private boolean trustEmail = false;
    }
}
