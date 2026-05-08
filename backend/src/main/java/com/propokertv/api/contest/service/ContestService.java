package com.propokertv.api.contest.service;

import com.propokertv.api.clip.domain.ClipVisibility;
import com.propokertv.api.clip.domain.ModerationStatus;
import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.common.error.ConflictException;
import com.propokertv.api.common.error.DomainException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.contest.domain.*;
import com.propokertv.api.contest.dto.ContestDtos.*;
import com.propokertv.api.contest.repo.VoteRepository;
import com.propokertv.api.contest.repo.WeeklyContestEntryRepository;
import com.propokertv.api.contest.repo.WeeklyContestRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContestService {
    private final WeeklyContestRepository contestRepository;
    private final WeeklyContestEntryRepository entryRepository;
    private final VoteRepository voteRepository;
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;
    private final AnalyticsEventService analyticsEventService;

    @Transactional
    public ContestResponse create(CreateContestRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new DomainException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Contest end must be after start");
        }
        WeeklyContest contest = new WeeklyContest();
        contest.setTitle(request.title());
        contest.setStartsAt(request.startsAt());
        contest.setEndsAt(request.endsAt());
        contest.setStatus(ContestStatus.DRAFT);
        return toResponse(contestRepository.save(contest));
    }

    @Transactional
    public ContestResponse open(Long contestId) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        if (contest.getStatus() != ContestStatus.DRAFT && contest.getStatus() != ContestStatus.CLOSED) {
            throw new ConflictException(ErrorCode.CONFLICT, "Only draft or closed contests can be opened");
        }
        contest.setStatus(ContestStatus.OPEN);
        return toResponse(contestRepository.save(contest));
    }

    @Transactional
    public ContestResponse nominate(Long contestId, NominateClipRequest request) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        requireNotFinalized(contest);
        var clip = clipRepository.findById(request.clipId()).orElseThrow(() -> new NotFoundException("Clip not found"));
        if (clip.getModerationStatus() != ModerationStatus.APPROVED || clip.getVisibility() != ClipVisibility.PUBLIC || clip.isDeleted()) {
            throw new DomainException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Only approved public clips can be nominated");
        }
        if (entryRepository.existsByWeeklyContestIdAndClipId(contestId, request.clipId())) {
            throw new ConflictException(ErrorCode.CONFLICT, "Clip is already nominated for this contest");
        }
        WeeklyContestEntry entry = new WeeklyContestEntry();
        entry.setWeeklyContest(contest);
        entry.setClip(clip);
        entryRepository.save(entry);
        analyticsEventService.track("clip_nominated", Map.of("contestId", contestId, "clipId", clip.getId()));
        return toResponse(contest);
    }

    @Transactional
    public ContestResponse vote(Long voterUserId, Long contestId, VoteRequest request) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        requireVotingOpen(contest);
        voteRepository.findByWeeklyContestIdAndVoterUserId(contestId, voterUserId)
                .ifPresent(existing -> { throw new ConflictException(ErrorCode.ALREADY_VOTED, "Voter has already voted in this contest"); });

        var entry = entryRepository.findById(request.entryId()).orElseThrow(() -> new NotFoundException("Contest entry not found"));
        if (!entry.getWeeklyContest().getId().equals(contestId)) {
            throw new DomainException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Contest entry does not belong to contest");
        }
        var voter = userRepository.findById(voterUserId).orElseThrow(() -> new NotFoundException("User not found"));
        Vote vote = new Vote();
        vote.setWeeklyContest(contest);
        vote.setEntry(entry);
        vote.setVoterUser(voter);
        voteRepository.save(vote);
        analyticsEventService.track("contest_vote_cast", Map.of("contestId", contestId, "entryId", entry.getId(), "voterUserId", voterUserId));
        return toResponse(contest);
    }

    @Transactional
    public ContestResponse finalizeContest(Long contestId) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        if (contest.getStatus() == ContestStatus.FINALIZED) {
            return toResponse(contest);
        }
        if (contest.getStatus() == ContestStatus.DRAFT) {
            throw new ConflictException(ErrorCode.CONFLICT, "Draft contests cannot be finalized");
        }

        var entries = entryRepository.findByWeeklyContestId(contestId);
        if (entries.isEmpty()) {
            throw new ConflictException(ErrorCode.CONFLICT, "Contest has no nominees");
        }

        var winner = entries.stream()
                .max(Comparator
                        .comparingLong((WeeklyContestEntry entry) -> voteRepository.countByEntryId(entry.getId()))
                        .thenComparing(WeeklyContestEntry::getId, Comparator.reverseOrder()))
                .orElseThrow();
        contest.setWinnerEntry(winner);
        contest.setStatus(ContestStatus.FINALIZED);
        contest.setFinalizedAt(Instant.now());
        analyticsEventService.track("contest_finalized", Map.of("contestId", contestId, "winnerEntryId", winner.getId(), "winnerClipId", winner.getClip().getId()));
        return toResponse(contestRepository.save(contest));
    }

    @Transactional(readOnly = true)
    public ContestResponse getOpenContest() {
        return contestRepository.findFirstByStatusOrderByStartsAtDesc(ContestStatus.OPEN)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("No open contest"));
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> winnerHistory() {
        return contestRepository.findByStatusOrderByFinalizedAtDesc(ContestStatus.FINALIZED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void requireNotFinalized(WeeklyContest contest) {
        if (contest.getStatus() == ContestStatus.FINALIZED) {
            throw new ConflictException(ErrorCode.CONFLICT, "Finalized contests cannot be changed");
        }
    }

    private void requireVotingOpen(WeeklyContest contest) {
        var now = Instant.now();
        if (contest.getStatus() != ContestStatus.OPEN || now.isBefore(contest.getStartsAt()) || now.isAfter(contest.getEndsAt())) {
            throw new ConflictException(ErrorCode.CONFLICT, "Contest is not open for voting");
        }
    }

    private ContestResponse toResponse(WeeklyContest contest) {
        List<ContestEntryResponse> entries = entryRepository.findByWeeklyContestId(contest.getId())
                .stream()
                .map(entry -> new ContestEntryResponse(entry.getId(), entry.getClip().getId(), voteRepository.countByEntryId(entry.getId())))
                .toList();
        var winner = contest.getWinnerEntry();
        return new ContestResponse(
                contest.getId(),
                contest.getTitle(),
                contest.getStatus().name(),
                contest.getStartsAt(),
                contest.getEndsAt(),
                contest.getFinalizedAt(),
                winner == null ? null : winner.getId(),
                winner == null ? null : winner.getClip().getId(),
                winner == null ? null : winner.getClip().getOwnerUser().getId(),
                entries
        );
    }
}
