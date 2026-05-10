package com.propokertv.api.calendar.domain;

import com.propokertv.api.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "poker_event")
public class PokerEvent extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(name = "organizer_name", nullable = false, length = 140)
    private String organizerName;

    @Column(name = "organizer_type", nullable = false, length = 60)
    private String organizerType;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(nullable = false, length = 80)
    private String timezone;

    @Column(name = "location_type", nullable = false, length = 30)
    private String locationType;

    @Column(length = 80)
    private String country;

    @Column(length = 80)
    private String city;

    @Column(name = "venue_name", length = 140)
    private String venueName;

    @Column(name = "online_url", length = 500)
    private String onlineUrl;

    @Column(name = "registration_url", length = 500)
    private String registrationUrl;

    @Column(name = "affiliate_url", length = 500)
    private String affiliateUrl;

    @Column(name = "affiliate_disclosure_required", nullable = false)
    private boolean affiliateDisclosureRequired = false;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    @Column(name = "tags_csv", length = 500)
    private String tagsCsv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventStatus status = EventStatus.DRAFT;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private boolean sponsored = false;
}
