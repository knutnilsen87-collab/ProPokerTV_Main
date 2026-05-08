package com.propokertv.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class CommentDtos {
    public record CreateCommentRequest(Long parentCommentId, @NotBlank @Size(max = 1000) String body) {}
    public record CommentResponse(Long id, Long clipId, Long authorUserId, Long parentCommentId, String body, Instant createdAt) {}
}
