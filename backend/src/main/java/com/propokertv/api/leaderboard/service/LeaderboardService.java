package com.propokertv.api.leaderboard.service;

import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.leaderboard.dto.LeaderboardDtos.LeaderboardRow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final ClipRepository clipRepository;

    @Transactional(readOnly = true)
    public List<LeaderboardRow> topClips(int size) {
        return clipRepository.findByModerationStatusAndDeletedAtIsNull(com.propokertv.api.clip.domain.ModerationStatus.APPROVED, PageRequest.of(0, size))
                .stream()
                .map(clip -> new LeaderboardRow(clip.getId(), clip.getTitle(), clip.getViewCount()))
                .toList();
    }
}
