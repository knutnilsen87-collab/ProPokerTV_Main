package com.propokertv.api.contest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public class ContestDtos {
    public record CreateContestRequest(@NotBlank String title, @NotNull Instant startsAt, @NotNull Instant endsAt) {}
    public record NominateClipRequest(@NotNull Long clipId) {}
    public record VoteRequest(@NotNull Long entryId) {}
    public record ContestEntryResponse(Long entryId, Long clipId, long votes) {}
    public record ContestResponse(Long id, String title, String status, Instant startsAt, Instant endsAt, List<ContestEntryResponse> entries) {}
}
