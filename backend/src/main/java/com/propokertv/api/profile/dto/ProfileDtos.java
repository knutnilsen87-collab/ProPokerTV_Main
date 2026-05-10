package com.propokertv.api.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ProfileDtos {
    public record UpdateProfileRequest(
            @NotBlank @Size(min = 3, max = 40)
            @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscore")
            String username,
            @NotBlank @Size(min = 2, max = 80)
            String displayName,
            @Size(max = 400)
            String bio,
            @Size(max = 500)
            String avatarUrl,
            @Size(max = 500)
            String bannerUrl,
            @Size(max = 80)
            String country,
            @Size(max = 80)
            String city,
            @Size(max = 8)
            List<@Size(max = 40) String> languages,
            @Size(max = 40)
            String profileType,
            @Size(max = 10)
            List<@Size(max = 80) String> pokerRoles,
            @Size(max = 10)
            List<@Size(max = 80) String> preferredGames,
            @Size(max = 10)
            List<@Size(max = 80) String> preferredFormats,
            @Size(max = 10)
            List<@Size(max = 80) String> contentFocus,
            @Size(max = 120)
            String preferredRegion,
            @Size(max = 10)
            List<@Size(max = 80) String> interestedEventTypes,
            Boolean onlineEventsAllowed,
            Integer maxTravelDistanceKm,
            Boolean eventAlertsOptIn,
            Boolean partnerOffersOptIn
    ) {}

    public record ProfileResponse(
            Long userId,
            String username,
            String displayName,
            String bio,
            String avatarUrl,
            String bannerUrl,
            String country,
            String city,
            List<String> languages,
            String profileType,
            List<String> pokerRoles,
            List<String> preferredGames,
            List<String> preferredFormats,
            List<String> contentFocus,
            String preferredRegion,
            List<String> interestedEventTypes,
            Boolean onlineEventsAllowed,
            Integer maxTravelDistanceKm,
            Boolean eventAlertsOptIn,
            Boolean partnerOffersOptIn
    ) {}
}
