package com.propokertv.api.contest.domain;

import com.propokertv.api.clip.domain.Clip;
import com.propokertv.api.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "weekly_contest_entry")
public class WeeklyContestEntry extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "weekly_contest_id")
    private WeeklyContest weeklyContest;

    @ManyToOne(optional = false) @JoinColumn(name = "clip_id")
    private Clip clip;
}
