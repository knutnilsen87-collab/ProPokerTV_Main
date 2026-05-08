package com.propokertv.api.creator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

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
            String socialLinksJson,
            CreatorReputationResponse reputation
    ) {}

    public record CreatorReputationResponse(
            long wins,
            long nominations,
            long totalContestVotes,
            Integer rankingPosition,
            String topCategory,
            List<String> badges
    ) {}
}
