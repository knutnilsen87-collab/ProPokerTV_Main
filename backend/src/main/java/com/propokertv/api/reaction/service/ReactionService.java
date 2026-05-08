package com.propokertv.api.reaction.service;

import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.reaction.domain.Reaction;
import com.propokertv.api.reaction.domain.ReactionType;
import com.propokertv.api.reaction.dto.ReactionDtos.ReactRequest;
import com.propokertv.api.reaction.dto.ReactionDtos.ReactionSummary;
import com.propokertv.api.reaction.repo.ReactionRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;
    private final AnalyticsEventService analyticsEventService;

    @Transactional
    public void react(Long userId, String clipSlug, ReactRequest request) {
        var clip = clipRepository.findBySlug(clipSlug).orElseThrow(() -> new NotFoundException("Clip not found"));
        reactionRepository.findByClipIdAndUserIdAndReactionType(clip.getId(), userId, request.reactionType())
                .ifPresent(existing -> { throw new IllegalStateException("Reaction already exists"); });
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Reaction reaction = new Reaction();
        reaction.setClip(clip);
        reaction.setUser(user);
        reaction.setReactionType(request.reactionType());
        var saved = reactionRepository.save(reaction);
        analyticsEventService.track("reaction_created", Map.of("reactionId", saved.getId(), "clipId", clip.getId(), "userId", userId, "reactionType", request.reactionType().name()));
    }

    @Transactional
    public void unreact(Long userId, String clipSlug, ReactionType reactionType) {
        var clip = clipRepository.findBySlug(clipSlug).orElseThrow(() -> new NotFoundException("Clip not found"));
        reactionRepository.findByClipIdAndUserIdAndReactionType(clip.getId(), userId, reactionType)
                .ifPresent(reactionRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<ReactionSummary> getSummary(Long maybeUserId, String clipSlug) {
        var clip = clipRepository.findBySlug(clipSlug).orElseThrow(() -> new NotFoundException("Clip not found"));
        return Arrays.stream(ReactionType.values()).map(type -> {
            boolean reacted = maybeUserId != null && reactionRepository.findByClipIdAndUserIdAndReactionType(clip.getId(), maybeUserId, type).isPresent();
            return new ReactionSummary(type.name(), reactionRepository.countByClipIdAndReactionType(clip.getId(), type), reacted);
        }).toList();
    }
}
