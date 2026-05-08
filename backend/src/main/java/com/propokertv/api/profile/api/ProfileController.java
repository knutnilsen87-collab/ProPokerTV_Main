package com.propokertv.api.profile.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import com.propokertv.api.profile.dto.ProfileDtos.ProfileResponse;
import com.propokertv.api.profile.dto.ProfileDtos.UpdateProfileRequest;
import com.propokertv.api.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ApiEnvelope<ProfileResponse> upsertMyProfile(@RequestBody @Valid UpdateProfileRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(profileService.upsert(currentUser.userId(), request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ApiEnvelope<ProfileResponse> getMyProfile(CurrentUser currentUser) {
        return ApiEnvelope.ok(profileService.getMyProfile(currentUser.userId()));
    }

    @GetMapping("/{username}")
    public ApiEnvelope<ProfileResponse> getPublicProfile(@PathVariable String username) {
        return ApiEnvelope.ok(profileService.getPublicProfile(username));
    }
}
