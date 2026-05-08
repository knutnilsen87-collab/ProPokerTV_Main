package com.propokertv.api.leaderboard.dto;

public class LeaderboardDtos {
    public record LeaderboardRow(Long subjectId, String label, long score) {}
}
