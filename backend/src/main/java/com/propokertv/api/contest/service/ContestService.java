package com.propokertv.api.contest.service;

import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.common.error.ConflictException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.contest.domain.*;
import com.propokertv.api.contest.dto.ContestDtos.*;
import com.propokertv.api.contest.repo.VoteRepository;
import com.propokertv.api.contest.repo.WeeklyContestEntryRepository;
import com.propokertv.api.contest.repo.WeeklyContestRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestService {
    private final WeeklyContestRepository contestRepository;
    private final WeeklyContestEntryRepository entryRepository;
    private final VoteRepository voteRepository;
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;

    @Transactional
    public ContestResponse create(CreateContestRequest request) {
        WeeklyContest contest = new WeeklyContest();
        contest.setTitle(request.title());
        contest.setStartsAt(request.startsAt());
        contest.setEndsAt(request.endsAt());
        contest.setStatus(ContestStatus.DRAFT);
        return toResponse(contestRepository.save(contest));
    }

    @Transactional
    public ContestResponse nominate(Long contestId, NominateClipRequest request) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        var clip = clipRepository.findById(request.clipId()).orElseThrow(() -> new NotFoundException("Clip not found"));
        WeeklyContestEntry entry = new WeeklyContestEntry();
        entry.setWeeklyContest(contest);
        entry.setClip(clip);
        entryRepository.save(entry);
        return toResponse(contest);
    }

    @Transactional
    public ContestResponse vote(Long voterUserId, Long contestId, VoteRequest request) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        voteRepository.findByWeeklyContestIdAndVoterUserId(contestId, voterUserId)
                .ifPresent(existing -> { throw new ConflictException(ErrorCode.ALREADY_VOTED, "Voter has already voted in this contest"); });

        var entry = entryRepository.findById(request.entryId()).orElseThrow(() -> new NotFoundException("Contest entry not found"));
        var voter = userRepository.findById(voterUserId).orElseThrow(() -> new NotFoundException("User not found"));
        Vote vote = new Vote();
        vote.setWeeklyContest(contest);
        vote.setEntry(entry);
        vote.setVoterUser(voter);
        voteRepository.save(vote);
        return toResponse(contest);
    }

    @Transactional(readOnly = true)
    public ContestResponse getOpenContest() {
        return contestRepository.findFirstByStatusOrderByStartsAtDesc(ContestStatus.OPEN)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("No open contest"));
    }

    private ContestResponse toResponse(WeeklyContest contest) {
        List<ContestEntryResponse> entries = entryRepository.findByWeeklyContestId(contest.getId())
                .stream()
                .map(entry -> new ContestEntryResponse(entry.getId(), entry.getClip().getId(), voteRepository.countByEntryId(entry.getId())))
                .toList();
        return new ContestResponse(contest.getId(), contest.getTitle(), contest.getStatus().name(), contest.getStartsAt(), contest.getEndsAt(), entries);
    }
}
