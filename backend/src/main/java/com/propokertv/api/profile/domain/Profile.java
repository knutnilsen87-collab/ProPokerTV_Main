package com.propokertv.api.profile.domain;

import com.propokertv.api.common.model.AuditableEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "profile")
public class Profile extends AuditableEntity {
    @Id
    private Long id;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    @Column(length = 400)
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(length = 80)
    private String country;

    @Column(length = 80)
    private String city;

    @Column(name = "languages_csv", length = 240)
    private String languagesCsv;

    @Column(name = "profile_type", length = 40)
    private String profileType;

    @Column(name = "poker_roles_csv", length = 500)
    private String pokerRolesCsv;

    @Column(name = "preferred_games_csv", length = 500)
    private String preferredGamesCsv;

    @Column(name = "preferred_formats_csv", length = 500)
    private String preferredFormatsCsv;

    @Column(name = "content_focus_csv", length = 500)
    private String contentFocusCsv;

    @Column(name = "preferred_region", length = 120)
    private String preferredRegion;

    @Column(name = "interested_event_types_csv", length = 500)
    private String interestedEventTypesCsv;

    @Column(name = "online_events_allowed", nullable = false)
    private boolean onlineEventsAllowed = true;

    @Column(name = "max_travel_distance_km")
    private Integer maxTravelDistanceKm;

    @Column(name = "event_alerts_opt_in", nullable = false)
    private boolean eventAlertsOptIn = false;

    @Column(name = "partner_offers_opt_in", nullable = false)
    private boolean partnerOffersOptIn = false;
}
