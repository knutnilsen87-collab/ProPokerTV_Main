package com.propokertv.api.auth.service;

import com.propokertv.api.auth.domain.RefreshToken;
import com.propokertv.api.auth.repo.RefreshTokenRepository;
import com.propokertv.api.common.error.DomainException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String create(User user, long ttlDays) {
        String plain = UUID.randomUUID() + "." + UUID.randomUUID();
        var token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(plain));
        token.setExpiresAt(Instant.now().plusSeconds(ttlDays * 86400));
        refreshTokenRepository.save(token);
        return plain;
    }

    @Transactional(readOnly = true)
    public RefreshToken findActive(String plainToken) {
        var token = refreshTokenRepository.findByTokenHash(hash(plainToken))
                .orElseThrow(() -> new DomainException(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, "Refresh token is invalid"));
        if (!token.isActive()) {
            throw new DomainException(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, "Refresh token is no longer active");
        }
        return token;
    }

    @Transactional
    public void revoke(String plainToken) {
        refreshTokenRepository.findByTokenHash(hash(plainToken)).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public String rotate(String plainToken, long ttlDays) {
        RefreshToken current = findActive(plainToken);
        String replacementPlain = UUID.randomUUID() + "." + UUID.randomUUID();
        String replacementHash = hash(replacementPlain);

        current.setRevokedAt(Instant.now());
        current.setReplacedByTokenHash(replacementHash);
        refreshTokenRepository.save(current);

        RefreshToken replacement = new RefreshToken();
        replacement.setUser(current.getUser());
        replacement.setTokenHash(replacementHash);
        replacement.setExpiresAt(Instant.now().plusSeconds(ttlDays * 86400));
        refreshTokenRepository.save(replacement);
        return replacementPlain;
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        Instant now = Instant.now();
        refreshTokenRepository.findByUserIdAndRevokedAtIsNull(userId)
                .forEach(token -> {
                    token.setRevokedAt(now);
                    refreshTokenRepository.save(token);
                });
    }

    private String hash(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }
}
