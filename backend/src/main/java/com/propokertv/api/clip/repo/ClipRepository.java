package com.propokertv.api.clip.repo;

import com.propokertv.api.clip.domain.Clip;
import com.propokertv.api.clip.domain.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClipRepository extends JpaRepository<Clip, Long> {
    Optional<Clip> findBySlug(String slug);
    Page<Clip> findByModerationStatusAndDeletedAtIsNull(ModerationStatus moderationStatus, Pageable pageable);
    Page<Clip> findByOwnerUserIdAndDeletedAtIsNull(Long ownerUserId, Pageable pageable);
}
