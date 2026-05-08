package com.propokertv.api.leaderboard.service;

import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.contest.repo.VoteRepository;
import com.propokertv.api.contest.repo.WeeklyContestEntryRepository;
import com.propokertv.api.contest.repo.WeeklyContestRepository;
import com.propokertv.api.creator.repo.CreatorProfileRepository;
import com.propokertv.api.leaderboard.dto.LeaderboardDtos.CreatorLeaderboardRow;
import com.propokertv.api.leaderboard.dto.LeaderboardDtos.LeaderboardRow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final ClipRepository clipRepository;
    private final CreatorProfileRepository creatorProfileRepository;
    private final WeeklyContestRepository contestRepository;
    private final WeeklyContestEntryRepository entryRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public List<LeaderboardRow> topClips(int size) {
        return clipRepository.findByModerationStatusAndDeletedAtIsNull(com.propokertv.api.clip.domain.ModerationStatus.APPROVED, PageRequest.of(0, size))
                .stream()
                .map(clip -> new LeaderboardRow(clip.getId(), clip.getTitle(), clip.getViewCount()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CreatorLeaderboardRow> topCreators(int size) {
        return creatorProfileRepository.findAll().stream()
                .map(creator -> {
                    long wins = contestRepository.countWinsForCreator(creator.getId());
                    long nominations = entryRepository.countNominationsForCreator(creator.getId());
                    long votes = voteRepository.countVotesForCreator(creator.getId());
                    long score = wins * 1000 + votes * 10 + nominations;
                    return new CreatorLeaderboardRow(creator.getId(), creator.getCreatorSlug(), wins, nominations, votes, score);
                })
                .sorted(Comparator.comparingLong(CreatorLeaderboardRow::score).reversed()
                        .thenComparing(CreatorLeaderboardRow::creatorSlug))
                .limit(size)
                .toList();
    }
}
