package com.propokertv.api.comment.repo;

import com.propokertv.api.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByClipIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long clipId);
}
