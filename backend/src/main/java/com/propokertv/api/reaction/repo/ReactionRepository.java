package com.propokertv.api.reaction.repo;

import com.propokertv.api.reaction.domain.Reaction;
import com.propokertv.api.reaction.domain.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByClipIdAndUserIdAndReactionType(Long clipId, Long userId, ReactionType reactionType);
    long countByClipIdAndReactionType(Long clipId, ReactionType reactionType);
}
