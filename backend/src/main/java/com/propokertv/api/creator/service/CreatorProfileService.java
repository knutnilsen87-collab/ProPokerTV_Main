package com.propokertv.api.creator.service;

import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.contest.repo.VoteRepository;
import com.propokertv.api.contest.repo.WeeklyContestEntryRepository;
import com.propokertv.api.contest.repo.WeeklyContestRepository;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.creator.domain.CreatorProfile;
import com.propokertv.api.creator.dto.CreatorProfileDtos.CreatorReputationResponse;
import com.propokertv.api.creator.dto.CreatorProfileDtos.CreatorProfileResponse;
import com.propokertv.api.creator.dto.CreatorProfileDtos.UpsertCreatorProfileRequest;
import com.propokertv.api.creator.repo.CreatorProfileRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreatorProfileService {
    private final CreatorProfileRepository creatorProfileRepository;
    private final UserRepository userRepository;
    private final WeeklyContestRepository contestRepository;
    private final WeeklyContestEntryRepository entryRepository;
    private final VoteRepository voteRepository;
    private final AnalyticsEventService analyticsEventService;

    @Transactional
    public CreatorProfileResponse upsert(Long userId, UpsertCreatorProfileRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var creator = creatorProfileRepository.findById(userId).orElseGet(CreatorProfile::new);
        creator.setUser(user);
        creator.setCreatorSlug(request.creatorSlug());
        creator.setHeadline(request.headline());
        creator.setSocialLinksJson(request.socialLinksJson());
        var saved = creatorProfileRepository.save(creator);
        analyticsEventService.track("creator_profile_completed", Map.of("userId", userId, "creatorSlug", saved.getCreatorSlug()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CreatorProfileResponse getBySlug(String slug) {
        return creatorProfileRepository.findByCreatorSlug(slug).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Creator profile not found"));
    }

    private CreatorProfileResponse toResponse(CreatorProfile creator) {
        return new CreatorProfileResponse(creator.getId(), creator.getCreatorSlug(), creator.getHeadline(),
                creator.isVerified(), creator.getSocialLinksJson(), reputationFor(creator));
    }

    private CreatorReputationResponse reputationFor(CreatorProfile creator) {
        long wins = contestRepository.countWinsForCreator(creator.getId());
        long nominations = entryRepository.countNominationsForCreator(creator.getId());
        long totalVotes = voteRepository.countVotesForCreator(creator.getId());
        String topCategory = entryRepository.topCategoriesForCreator(creator.getId()).stream()
                .map(row -> row[0] == null ? "uncategorized" : row[0].toString())
                .findFirst()
                .orElse(null);
        Integer ranking = rankingPositionFor(creator.getId());

        List<String> badges = new ArrayList<>();
        if (wins > 0) badges.add("Clip of the Week Winner");
        if (nominations > 0 && wins == 0) badges.add("Rising Creator");
        if (totalVotes >= 10) badges.add("Fan Favorite");

        return new CreatorReputationResponse(wins, nominations, totalVotes, ranking, topCategory, badges);
    }

    private Integer rankingPositionFor(Long creatorUserId) {
        var ranked = creatorProfileRepository.findAll().stream()
                .sorted(Comparator
                        .comparingLong((CreatorProfile creator) -> contestRepository.countWinsForCreator(creator.getId())).reversed()
                        .thenComparing(creator -> voteRepository.countVotesForCreator(creator.getId()), Comparator.reverseOrder())
                        .thenComparing(CreatorProfile::getCreatorSlug))
                .toList();
        for (int index = 0; index < ranked.size(); index++) {
            if (ranked.get(index).getId().equals(creatorUserId)) {
                return index + 1;
            }
        }
        return null;
    }
}
