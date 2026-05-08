package com.propokertv.api.auth.repo;

import com.propokertv.api.auth.domain.AuthIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, Long> {
    Optional<AuthIdentity> findByProviderAndProviderSubject(String provider, String providerSubject);
}
