package com.propokertv.api.reaction.dto;

import com.propokertv.api.reaction.domain.ReactionType;
import jakarta.validation.constraints.NotNull;

public class ReactionDtos {
    public record ReactRequest(@NotNull ReactionType reactionType) {}
    public record ReactionSummary(String reactionType, long count, boolean reactedByCurrentUser) {}
}
