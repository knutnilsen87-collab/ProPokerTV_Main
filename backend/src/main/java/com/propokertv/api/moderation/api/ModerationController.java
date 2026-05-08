package com.propokertv.api.moderation.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import com.propokertv.api.moderation.dto.ModerationDtos.*;
import com.propokertv.api.moderation.service.ModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
public class ModerationController {
    private final ModerationService moderationService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reports")
    public ApiEnvelope<ReportResponse> createReport(@RequestBody @Valid CreateReportRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(moderationService.createReport(currentUser.userId(), request));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN')")
    @GetMapping("/queue")
    public ApiEnvelope<List<ReportResponse>> openQueue() {
        return ApiEnvelope.ok(moderationService.openQueue());
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN')")
    @PostMapping("/clips/{clipId}/decision")
    public ApiEnvelope<String> moderateClip(@PathVariable Long clipId, @RequestBody @Valid ModerateClipRequest request) {
        return ApiEnvelope.ok(moderationService.moderateClip(clipId, request));
    }
}
