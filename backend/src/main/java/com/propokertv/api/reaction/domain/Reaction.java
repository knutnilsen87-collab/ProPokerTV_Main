package com.propokertv.api.reaction.domain;

import com.propokertv.api.clip.domain.Clip;
import com.propokertv.api.common.model.AuditableEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reaction", uniqueConstraints = @UniqueConstraint(name = "uq_reaction_clip_user_type", columnNames = {"clip_id", "user_id", "reaction_type"}))
public class Reaction extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "clip_id")
    private Clip clip;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 30)
    private ReactionType reactionType;
}
