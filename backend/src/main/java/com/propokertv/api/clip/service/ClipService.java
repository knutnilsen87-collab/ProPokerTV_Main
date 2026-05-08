package com.propokertv.api.clip.service;

import com.propokertv.api.clip.domain.Clip;
import com.propokertv.api.clip.domain.ClipVisibility;
import com.propokertv.api.clip.domain.ModerationStatus;
import com.propokertv.api.clip.dto.ClipDtos.*;
import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.common.error.ConflictException;
import com.propokertv.api.common.error.DomainException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.common.error.ForbiddenException;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClipService {
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;
    private final AnalyticsEventService analyticsEventService;

    @Transactional
    public ClipResponse create(Long userId, CreateClipRequest request) {
        clipRepository.findBySlug(request.slug()).ifPresent(existing -> {
            throw new ConflictException(ErrorCode.CONFLICT, "Clip slug already exists");
        });
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Clip clip = new Clip();
        clip.setOwnerUser(user);
        clip.setSlug(request.slug());
        clip.setTitle(request.title());
        clip.setDescription(request.description());
        clip.setVisibility(request.visibility() == null ? ClipVisibility.PUBLIC : request.visibility());
        clip.setCategorySlug(request.categorySlug());
        clip.setTagsCsv(request.tagsCsv());
        requireSafeMediaUrl(request.thumbnailUrl(), "thumbnailUrl");
        requireSafeMediaUrl(request.playbackUrl(), "playbackUrl");
        clip.setThumbnailUrl(request.thumbnailUrl());
        clip.setPlaybackUrl(request.playbackUrl());
        clip.setDurationSeconds(request.durationSeconds());
        clip.setModerationStatus(ModerationStatus.PENDING_REVIEW);
        var saved = clipRepository.save(clip);
        analyticsEventService.track("clip_created", Map.of("clipId", saved.getId(), "creatorUserId", userId));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClipResponse> listPublicApproved(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        return clipRepository.findByModerationStatusAndDeletedAtIsNull(ModerationStatus.APPROVED, PageRequest.of(safePage, safeSize))
                .stream().filter(clip -> clip.getVisibility() == ClipVisibility.PUBLIC).map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClipResponse getPublicBySlug(String slug) {
        var clip = clipRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException("Clip not found"));
        if (clip.getModerationStatus() != ModerationStatus.APPROVED || clip.getVisibility() == ClipVisibility.PRIVATE || clip.isDeleted()) {
            throw new NotFoundException("Clip not found");
        }
        return toResponse(clip);
    }

    @Transactional(readOnly = true)
    public ClipResponse getOwnedBySlug(Long userId, String slug) {
        var clip = clipRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException("Clip not found"));
        requireOwner(userId, clip);
        return toResponse(clip);
    }

    @Transactional
    public ClipResponse updateOwned(Long userId, String slug, UpdateClipRequest request) {
        var clip = clipRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException("Clip not found"));
        requireOwner(userId, clip);
        clip.setTitle(request.title());
        clip.setDescription(request.description());
        if (request.visibility() != null) clip.setVisibility(request.visibility());
        clip.setCategorySlug(request.categorySlug());
        clip.setTagsCsv(request.tagsCsv());
        requireSafeMediaUrl(request.thumbnailUrl(), "thumbnailUrl");
        requireSafeMediaUrl(request.playbackUrl(), "playbackUrl");
        clip.setThumbnailUrl(request.thumbnailUrl());
        clip.setPlaybackUrl(request.playbackUrl());
        clip.setDurationSeconds(request.durationSeconds());
        return toResponse(clipRepository.save(clip));
    }

    @Transactional
    public void softDeleteOwned(Long userId, String slug) {
        var clip = clipRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException("Clip not found"));
        requireOwner(userId, clip);
        clip.setDeletedAt(Instant.now());
        clipRepository.save(clip);
    }

    private void requireOwner(Long userId, Clip clip) {
        if (!clip.getOwnerUser().getId().equals(userId)) {
            throw new ForbiddenException("Ownership required");
        }
    }

    private void requireSafeMediaUrl(String value, String field) {
        if (value == null || value.isBlank()) {
            return;
        }
        String normalized = value.toLowerCase();
        if (!(normalized.startsWith("https://") || normalized.startsWith("http://localhost") || normalized.startsWith("http://127.0.0.1"))) {
            throw new DomainException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, field + " must be HTTPS or a local development URL");
        }
    }

    private ClipResponse toResponse(Clip clip) {
        return new ClipResponse(clip.getId(), clip.getOwnerUser().getId(), clip.getSlug(), clip.getTitle(), clip.getDescription(),
                clip.getVisibility().name(), clip.getModerationStatus().name(), clip.getCategorySlug(), clip.getTagsCsv(),
                clip.getThumbnailUrl(), clip.getPlaybackUrl(), clip.getDurationSeconds(), clip.getViewCount());
    }
}
