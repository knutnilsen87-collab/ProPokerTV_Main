package com.propokertv.api.creator.service;

import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.creator.domain.CreatorProfile;
import com.propokertv.api.creator.dto.CreatorProfileDtos.CreatorProfileResponse;
import com.propokertv.api.creator.dto.CreatorProfileDtos.UpsertCreatorProfileRequest;
import com.propokertv.api.creator.repo.CreatorProfileRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorProfileService {
    private final CreatorProfileRepository creatorProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreatorProfileResponse upsert(Long userId, UpsertCreatorProfileRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var creator = creatorProfileRepository.findById(userId).orElseGet(CreatorProfile::new);
        creator.setUser(user);
        creator.setCreatorSlug(request.creatorSlug());
        creator.setHeadline(request.headline());
        creator.setSocialLinksJson(request.socialLinksJson());
        var saved = creatorProfileRepository.save(creator);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CreatorProfileResponse getBySlug(String slug) {
        return creatorProfileRepository.findByCreatorSlug(slug).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Creator profile not found"));
    }

    private CreatorProfileResponse toResponse(CreatorProfile creator) {
        return new CreatorProfileResponse(creator.getId(), creator.getCreatorSlug(), creator.getHeadline(),
                creator.isVerified(), creator.getSocialLinksJson());
    }
}
