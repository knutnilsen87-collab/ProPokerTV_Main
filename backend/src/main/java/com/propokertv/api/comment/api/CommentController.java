package com.propokertv.api.comment.api;

import com.propokertv.api.comment.dto.CommentDtos.CommentResponse;
import com.propokertv.api.comment.dto.CommentDtos.CreateCommentRequest;
import com.propokertv.api.comment.service.CommentService;
import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/clip/{clipSlug}")
    public ApiEnvelope<List<CommentResponse>> listByClip(@PathVariable String clipSlug) {
        return ApiEnvelope.ok(commentService.listByClip(clipSlug));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/clip/{clipSlug}")
    public ApiEnvelope<CommentResponse> create(@PathVariable String clipSlug, @RequestBody @Valid CreateCommentRequest request, CurrentUser currentUser) {
        return ApiEnvelope.ok(commentService.create(currentUser.userId(), clipSlug, request));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{commentId}")
    public ApiEnvelope<String> delete(@PathVariable Long commentId, CurrentUser currentUser) {
        boolean moderator = "MODERATOR".equals(currentUser.role()) || "ADMIN".equals(currentUser.role());
        commentService.deleteOwned(currentUser.userId(), commentId, moderator);
        return ApiEnvelope.ok("Comment deleted");
    }
}
