package com.propokertv.api.comment.domain;

import com.propokertv.api.clip.domain.Clip;
import com.propokertv.api.common.model.SoftDeleteEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "clip_id")
    private Clip clip;

    @ManyToOne(optional = false) @JoinColumn(name = "author_user_id")
    private User authorUser;

    @ManyToOne @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false, length = 1000)
    private String body;
}
