package com.propokertv.api.clip.domain;

import com.propokertv.api.common.model.SoftDeleteEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clip")
public class Clip extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @Column(nullable = false, unique = true, length = 160)
    private String slug;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClipVisibility visibility = ClipVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, length = 30)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING_REVIEW;

    @Column(name = "category_slug", length = 60)
    private String categorySlug;

    @Column(name = "tags_csv", length = 500)
    private String tagsCsv;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "playback_url", length = 500)
    private String playbackUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;
}
