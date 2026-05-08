package com.propokertv.api.leaderboard.dto;

public class LeaderboardDtos {
    public record LeaderboardRow(Long subjectId, String label, long score) {}
    public record CreatorLeaderboardRow(Long userId, String creatorSlug, long wins, long nominations, long totalContestVotes, long score) {}
}
