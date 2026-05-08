package com.propokertv.api.contest.domain;

import com.propokertv.api.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "weekly_contest")
public class WeeklyContest extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContestStatus status = ContestStatus.DRAFT;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @ManyToOne
    @JoinColumn(name = "winner_entry_id")
    private WeeklyContestEntry winnerEntry;

    @Column(name = "finalized_at")
    private Instant finalizedAt;
}
