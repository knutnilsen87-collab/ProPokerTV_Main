package com.propokertv.api.auth.service;

import com.propokertv.api.auth.domain.AuthActionToken;
import com.propokertv.api.auth.domain.AuthActionTokenType;
import com.propokertv.api.auth.repo.AuthActionTokenRepository;
import com.propokertv.api.common.error.DomainException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthActionTokenService {
    private static final Logger log = LoggerFactory.getLogger(AuthActionTokenService.class);

    private final AuthActionTokenRepository authActionTokenRepository;
    private final Environment environment;

    @Transactional
    public String issue(User user, AuthActionTokenType tokenType, Duration ttl) {
        invalidateActiveTokens(user.getId(), tokenType);

        String plain = UUID.randomUUID() + "." + UUID.randomUUID();
        AuthActionToken token = new AuthActionToken();
        token.setUser(user);
        token.setTokenType(tokenType);
        token.setTokenHash(hash(plain));
        token.setExpiresAt(Instant.now().plus(ttl));
        authActionTokenRepository.save(token);

        if (environment.matchesProfiles("local", "default", "test")) {
            log.info("LOCAL_AUTH_TOKEN type={} userId={} email={} token={}",
                    tokenType.name(), user.getId(), user.getEmail(), plain);
        }

        return plain;
    }

    @Transactional(readOnly = true)
    public AuthActionToken findActive(AuthActionTokenType tokenType, String plainToken) {
        AuthActionToken token = authActionTokenRepository.findByTokenHash(hash(plainToken))
                .orElseThrow(() -> invalidToken(tokenType));
        if (token.getTokenType() != tokenType || !token.isActive()) {
            throw invalidToken(tokenType);
        }
        return token;
    }

    @Transactional
    public AuthActionToken consume(AuthActionTokenType tokenType, String plainToken) {
        AuthActionToken token = findActive(tokenType, plainToken);
        token.setConsumedAt(Instant.now());
        return authActionTokenRepository.save(token);
    }

    @Transactional
    public void invalidateActiveTokens(Long userId, AuthActionTokenType tokenType) {
        Instant now = Instant.now();
        authActionTokenRepository.findByUserIdAndTokenTypeAndConsumedAtIsNull(userId, tokenType)
                .forEach(token -> {
                    token.setConsumedAt(now);
                    authActionTokenRepository.save(token);
                });
    }

    private DomainException invalidToken(AuthActionTokenType tokenType) {
        return new DomainException(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED,
                tokenType.name() + " token is invalid");
    }

    private String hash(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash auth action token", e);
        }
    }
}
