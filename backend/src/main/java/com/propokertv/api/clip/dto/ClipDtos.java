package com.propokertv.api.clip.dto;

import com.propokertv.api.clip.domain.ClipVisibility;
import com.propokertv.api.clip.domain.ModerationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClipDtos {
    public record CreateClipRequest(
            @NotBlank @Size(max = 160) String slug,
            @NotBlank @Size(max = 140) String title,
            @Size(max = 1000) String description,
            ClipVisibility visibility,
            @Size(max = 60) String categorySlug,
            @Size(max = 500) String tagsCsv,
            @Size(max = 500) String thumbnailUrl,
            @Size(max = 500) String playbackUrl,
            Integer durationSeconds
    ) {}

    public record UpdateClipRequest(
            @NotBlank @Size(max = 140) String title,
            @Size(max = 1000) String description,
            ClipVisibility visibility,
            @Size(max = 60) String categorySlug,
            @Size(max = 500) String tagsCsv,
            @Size(max = 500) String thumbnailUrl,
            @Size(max = 500) String playbackUrl,
            Integer durationSeconds
    ) {}

    public record ClipResponse(
            Long id,
            Long ownerUserId,
            String slug,
            String title,
            String description,
            String visibility,
            String moderationStatus,
            String categorySlug,
            String tagsCsv,
            String thumbnailUrl,
            String playbackUrl,
            Integer durationSeconds,
            long viewCount
    ) {}
}
