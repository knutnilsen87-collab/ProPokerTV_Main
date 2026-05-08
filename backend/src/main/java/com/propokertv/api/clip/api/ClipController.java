package com.propokertv.api.clip.api;

import com.propokertv.api.clip.dto.ClipDtos.*;
import com.propokertv.api.clip.service.ClipService;
import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clips")
@RequiredArgsConstructor
public class ClipController {
    private final ClipService clipService;

    @GetMapping
    public ApiEnvelope<List<ClipResponse>> listPublic(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        return ApiEnvelope.ok(clipService.listPublicApproved(page, size));
    }

    @GetMapping("/{slug}")
    public ApiEnvelope<ClipResponse> getPublic(@PathVariable String slug) {
        return ApiEnvelope.ok(clipService.getPublicBySlug(slug));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ApiEnvelope<ClipResponse> create(@RequestBody @Valid CreateClipRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(clipService.create(currentUser.userId(), request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/{slug}")
    public ApiEnvelope<ClipResponse> getOwned(@PathVariable String slug, CurrentUser currentUser) {
        return ApiEnvelope.ok(clipService.getOwnedBySlug(currentUser.userId(), slug));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{slug}")
    public ApiEnvelope<ClipResponse> update(@PathVariable String slug, @RequestBody @Valid UpdateClipRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(clipService.updateOwned(currentUser.userId(), slug, request));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{slug}")
    public ApiEnvelope<String> delete(@PathVariable String slug, CurrentUser currentUser) {
        clipService.softDeleteOwned(currentUser.userId(), slug);
        return ApiEnvelope.ok("Clip deleted");
    }
}
