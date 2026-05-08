package com.propokertv.api.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
            String bannerUrl
    ) {}

    public record ProfileResponse(
            Long userId,
            String username,
            String displayName,
            String bio,
            String avatarUrl,
            String bannerUrl
    ) {}
}
