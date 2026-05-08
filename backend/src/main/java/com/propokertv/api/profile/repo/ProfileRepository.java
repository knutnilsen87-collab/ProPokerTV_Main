package com.propokertv.api.profile.repo;

import com.propokertv.api.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
