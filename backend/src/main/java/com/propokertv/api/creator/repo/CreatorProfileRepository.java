package com.propokertv.api.creator.repo;

import com.propokertv.api.creator.domain.CreatorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CreatorProfileRepository extends JpaRepository<CreatorProfile, Long> {
    Optional<CreatorProfile> findByCreatorSlug(String creatorSlug);
}
