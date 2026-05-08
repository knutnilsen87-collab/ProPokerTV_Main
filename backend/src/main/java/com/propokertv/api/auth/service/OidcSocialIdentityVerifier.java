package com.propokertv.api.auth.service;

import com.propokertv.api.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OidcSocialIdentityVerifier implements SocialIdentityVerifier {
    private final SocialAuthProperties properties;
    private final Map<String, JwtDecoder> decoders = new ConcurrentHashMap<>();

    @Override
    public SocialIdentity verify(String provider, String idToken) {
        String normalizedProvider = provider.toLowerCase(Locale.ROOT);
        var providerConfig = properties.getProviders().get(normalizedProvider);
        if (providerConfig == null || !StringUtils.hasText(providerConfig.getClientId())) {
            throw new com.propokertv.api.common.error.DomainException(
                    ErrorCode.VALIDATION_ERROR,
                    HttpStatus.BAD_REQUEST,
                    "Social login provider is not configured"
            );
        }

        try {
            Jwt jwt = decoderFor(normalizedProvider, providerConfig).decode(idToken);
            String subject = jwt.getSubject();
            String email = firstTextClaim(jwt, "email", "preferred_username", "upn");
            boolean emailVerified = Boolean.TRUE.equals(jwt.getClaim("email_verified")) || providerConfig.isTrustEmail();
            if (!StringUtils.hasText(subject) || !StringUtils.hasText(email)) {
                throw new BadCredentialsException("Invalid social identity");
            }
            if (!emailVerified) {
                throw new BadCredentialsException("Social account email is not verified");
            }
            return new SocialIdentity(normalizedProvider, subject, email.toLowerCase(Locale.ROOT), true);
        } catch (JwtException ex) {
            throw new BadCredentialsException("Invalid social identity", ex);
        }
    }

    private JwtDecoder decoderFor(String provider, SocialAuthProperties.Provider providerConfig) {
        return decoders.computeIfAbsent(provider, ignored -> {
            NimbusJwtDecoder decoder = StringUtils.hasText(providerConfig.getJwkSetUri())
                    ? NimbusJwtDecoder.withJwkSetUri(providerConfig.getJwkSetUri()).build()
                    : NimbusJwtDecoder.withIssuerLocation(providerConfig.getIssuer()).build();
            OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(providerConfig.getIssuer());
            OAuth2TokenValidator<Jwt> audienceValidator = audienceValidator(providerConfig.getClientId());
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));
            return decoder;
        });
    }

    private OAuth2TokenValidator<Jwt> audienceValidator(String clientId) {
        return jwt -> jwt.getAudience().contains(clientId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "ID token audience does not match this app", null));
    }

    private String firstTextClaim(Jwt jwt, String... claimNames) {
        for (String claimName : claimNames) {
            String value = jwt.getClaimAsString(claimName);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}
