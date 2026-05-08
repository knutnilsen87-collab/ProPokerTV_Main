package com.propokertv.api.leaderboard.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.leaderboard.dto.LeaderboardDtos.LeaderboardRow;
import com.propokertv.api.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboards")
@RequiredArgsConstructor
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @GetMapping("/top-clips")
    public ApiEnvelope<List<LeaderboardRow>> topClips(@RequestParam(defaultValue = "10") int size) {
        return ApiEnvelope.ok(leaderboardService.topClips(size));
    }
}
