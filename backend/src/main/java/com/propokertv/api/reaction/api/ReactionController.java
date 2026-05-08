package com.propokertv.api.reaction.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import com.propokertv.api.reaction.domain.ReactionType;
import com.propokertv.api.reaction.dto.ReactionDtos.ReactRequest;
import com.propokertv.api.reaction.dto.ReactionDtos.ReactionSummary;
import com.propokertv.api.reaction.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @GetMapping("/clip/{clipSlug}")
    public ApiEnvelope<List<ReactionSummary>> summary(@PathVariable String clipSlug, CurrentUser currentUser) {
        Long currentUserId = currentUser != null ? currentUser.userId() : null;
        return ApiEnvelope.ok(reactionService.getSummary(currentUserId, clipSlug));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/clip/{clipSlug}")
    public ApiEnvelope<String> react(@PathVariable String clipSlug, @RequestBody @Valid ReactRequest request, CurrentUser currentUser) {
        reactionService.react(currentUser.userId(), clipSlug, request);
        return ApiEnvelope.ok("Reaction created");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/clip/{clipSlug}/{reactionType}")
    public ApiEnvelope<String> unreact(@PathVariable String clipSlug, @PathVariable ReactionType reactionType, CurrentUser currentUser) {
        reactionService.unreact(currentUser.userId(), clipSlug, reactionType);
        return ApiEnvelope.ok("Reaction removed");
    }
}
