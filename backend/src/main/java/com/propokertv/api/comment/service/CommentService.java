package com.propokertv.api.comment.service;

import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.comment.domain.Comment;
import com.propokertv.api.comment.dto.CommentDtos.*;
import com.propokertv.api.comment.repo.CommentRepository;
import com.propokertv.api.common.error.ForbiddenException;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(Long userId, String clipSlug, CreateCommentRequest request) {
        var clip = clipRepository.findBySlug(clipSlug).orElseThrow(() -> new NotFoundException("Clip not found"));
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Comment comment = new Comment();
        comment.setClip(clip);
        comment.setAuthorUser(user);
        comment.setBody(request.body());
        if (request.parentCommentId() != null) {
            var parent = commentRepository.findById(request.parentCommentId()).orElseThrow(() -> new NotFoundException("Parent comment not found"));
            comment.setParentComment(parent);
        }
        return toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listByClip(String clipSlug) {
        var clip = clipRepository.findBySlug(clipSlug).orElseThrow(() -> new NotFoundException("Clip not found"));
        return commentRepository.findByClipIdAndDeletedAtIsNullOrderByCreatedAtAsc(clip.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteOwned(Long userId, Long commentId, boolean moderator) {
        var comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!moderator && !comment.getAuthorUser().getId().equals(userId)) {
            throw new ForbiddenException("Ownership required");
        }
        comment.setDeletedAt(Instant.now());
        commentRepository.save(comment);
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getClip().getId(), comment.getAuthorUser().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getBody(), comment.getCreatedAt());
    }
}
