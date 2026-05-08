package com.propokertv.api.creator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatorProfileDtos {
    public record UpsertCreatorProfileRequest(
            @NotBlank @Size(min = 3, max = 80)
            String creatorSlug,
            @Size(max = 120)
            String headline,
            String socialLinksJson
    ) {}

    public record CreatorProfileResponse(
            Long userId,
            String creatorSlug,
            String headline,
            boolean verified,
            String socialLinksJson
    ) {}
}
