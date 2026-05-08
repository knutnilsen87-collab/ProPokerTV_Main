package com.propokertv.api.clip.service;

import com.propokertv.api.clip.domain.Clip;
import com.propokertv.api.clip.domain.ClipVisibility;
import com.propokertv.api.clip.domain.ModerationStatus;
import com.propokertv.api.clip.dto.ClipDtos.*;
import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.common.error.ConflictException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.common.error.ForbiddenException;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClipService {
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;

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
        clip.setThumbnailUrl(request.thumbnailUrl());
        clip.setPlaybackUrl(request.playbackUrl());
        clip.setDurationSeconds(request.durationSeconds());
        clip.setModerationStatus(ModerationStatus.PENDING_REVIEW);
        return toResponse(clipRepository.save(clip));
    }

    @Transactional(readOnly = true)
    public List<ClipResponse> listPublicApproved(int page, int size) {
        return clipRepository.findByModerationStatusAndDeletedAtIsNull(ModerationStatus.APPROVED, PageRequest.of(page, size))
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

    private ClipResponse toResponse(Clip clip) {
        return new ClipResponse(clip.getId(), clip.getOwnerUser().getId(), clip.getSlug(), clip.getTitle(), clip.getDescription(),
                clip.getVisibility().name(), clip.getModerationStatus().name(), clip.getCategorySlug(), clip.getTagsCsv(),
                clip.getThumbnailUrl(), clip.getPlaybackUrl(), clip.getDurationSeconds(), clip.getViewCount());
    }
}
