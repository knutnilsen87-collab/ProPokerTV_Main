package com.propokertv.api.auth.repo;

import com.propokertv.api.auth.domain.AuthActionToken;
import com.propokertv.api.auth.domain.AuthActionTokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthActionTokenRepository extends JpaRepository<AuthActionToken, Long> {
    Optional<AuthActionToken> findByTokenHash(String tokenHash);

    List<AuthActionToken> findByUserIdAndTokenTypeAndConsumedAtIsNull(Long userId, AuthActionTokenType tokenType);
}
