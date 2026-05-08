package com.propokertv.api.contest.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import com.propokertv.api.contest.dto.ContestDtos.*;
import com.propokertv.api.contest.service.ContestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contests")
@RequiredArgsConstructor
public class ContestController {
    private final ContestService contestService;

    @GetMapping("/open")
    public ApiEnvelope<ContestResponse> getOpenContest() {
        return ApiEnvelope.ok(contestService.getOpenContest());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PostMapping
    public ApiEnvelope<ContestResponse> create(@RequestBody @Valid CreateContestRequest request) {
        return ApiEnvelope.ok(contestService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PostMapping("/{contestId}/entries")
    public ApiEnvelope<ContestResponse> nominate(@PathVariable Long contestId, @RequestBody @Valid NominateClipRequest request) {
        return ApiEnvelope.ok(contestService.nominate(contestId, request));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{contestId}/vote")
    public ApiEnvelope<ContestResponse> vote(@PathVariable Long contestId, @RequestBody @Valid VoteRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(contestService.vote(currentUser.userId(), contestId, request));
    }
}
