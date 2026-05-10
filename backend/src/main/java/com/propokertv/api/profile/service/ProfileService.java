package com.propokertv.api.profile.service;

import com.propokertv.api.common.error.ConflictException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.profile.domain.Profile;
import com.propokertv.api.profile.dto.ProfileDtos.ProfileResponse;
import com.propokertv.api.profile.dto.ProfileDtos.UpdateProfileRequest;
import com.propokertv.api.profile.repo.ProfileRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse upsert(Long userId, UpdateProfileRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        profileRepository.findByUsernameIgnoreCase(request.username())
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> { throw new ConflictException(ErrorCode.CONFLICT, "Username is already taken"); });

        var profile = profileRepository.findById(userId).orElseGet(Profile::new);
        profile.setUser(user);
        profile.setUsername(request.username());
        profile.setDisplayName(request.displayName());
        profile.setBio(request.bio());
        profile.setAvatarUrl(request.avatarUrl());
        profile.setBannerUrl(request.bannerUrl());
        profile.setCountry(request.country());
        profile.setCity(request.city());
        profile.setLanguagesCsv(toCsv(request.languages()));
        profile.setProfileType(request.profileType());
        profile.setPokerRolesCsv(toCsv(request.pokerRoles()));
        profile.setPreferredGamesCsv(toCsv(request.preferredGames()));
        profile.setPreferredFormatsCsv(toCsv(request.preferredFormats()));
        profile.setContentFocusCsv(toCsv(request.contentFocus()));
        profile.setPreferredRegion(request.preferredRegion());
        profile.setInterestedEventTypesCsv(toCsv(request.interestedEventTypes()));
        if (request.onlineEventsAllowed() != null) {
            profile.setOnlineEventsAllowed(request.onlineEventsAllowed());
        }
        profile.setMaxTravelDistanceKm(request.maxTravelDistanceKm());
        if (request.eventAlertsOptIn() != null) {
            profile.setEventAlertsOptIn(request.eventAlertsOptIn());
        }
        if (request.partnerOffersOptIn() != null) {
            profile.setPartnerOffersOptIn(request.partnerOffersOptIn());
        }
        var saved = profileRepository.save(profile);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long userId) {
        return profileRepository.findById(userId).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getPublicProfile(String username) {
        return profileRepository.findByUsernameIgnoreCase(username).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(profile.getId(), profile.getUsername(), profile.getDisplayName(),
                profile.getBio(), profile.getAvatarUrl(), profile.getBannerUrl(),
                profile.getCountry(), profile.getCity(), fromCsv(profile.getLanguagesCsv()), profile.getProfileType(),
                fromCsv(profile.getPokerRolesCsv()), fromCsv(profile.getPreferredGamesCsv()),
                fromCsv(profile.getPreferredFormatsCsv()), fromCsv(profile.getContentFocusCsv()),
                profile.getPreferredRegion(), fromCsv(profile.getInterestedEventTypesCsv()),
                profile.isOnlineEventsAllowed(), profile.getMaxTravelDistanceKm(),
                profile.isEventAlertsOptIn(), profile.isPartnerOffersOptIn());
    }

    private String toCsv(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(",", values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList());
    }

    private List<String> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
