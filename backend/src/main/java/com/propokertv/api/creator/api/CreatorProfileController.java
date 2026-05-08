package com.propokertv.api.creator.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import com.propokertv.api.creator.dto.CreatorProfileDtos.CreatorProfileResponse;
import com.propokertv.api.creator.dto.CreatorProfileDtos.UpsertCreatorProfileRequest;
import com.propokertv.api.creator.service.CreatorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/creators")
@RequiredArgsConstructor
public class CreatorProfileController {
    private final CreatorProfileService creatorProfileService;

    @PreAuthorize("hasAnyAuthority('CREATOR','ADMIN')")
    @PutMapping("/me")
    public ApiEnvelope<CreatorProfileResponse> upsert(@RequestBody @Valid UpsertCreatorProfileRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(creatorProfileService.upsert(currentUser.userId(), request));
    }

    @GetMapping("/{slug}")
    public ApiEnvelope<CreatorProfileResponse> getBySlug(@PathVariable String slug) {
        return ApiEnvelope.ok(creatorProfileService.getBySlug(slug));
    }
}
