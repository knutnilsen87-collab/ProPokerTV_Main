package com.propokertv.api.creator.domain;

import com.propokertv.api.common.model.AuditableEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "creator_profile")
public class CreatorProfile extends AuditableEntity {
    @Id
    private Long id;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "creator_slug", nullable = false, unique = true, length = 80)
    private String creatorSlug;

    @Column(name = "headline", length = 120)
    private String headline;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "social_links_json", columnDefinition = "TEXT")
    private String socialLinksJson;
}
