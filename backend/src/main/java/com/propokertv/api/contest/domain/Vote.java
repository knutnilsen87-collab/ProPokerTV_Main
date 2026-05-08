package com.propokertv.api.contest.domain;

import com.propokertv.api.common.model.AuditableEntity;
import com.propokertv.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vote", uniqueConstraints = @UniqueConstraint(name = "uq_vote_contest_voter", columnNames = {"weekly_contest_id", "voter_user_id"}))
public class Vote extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "weekly_contest_id")
    private WeeklyContest weeklyContest;

    @ManyToOne(optional = false) @JoinColumn(name = "entry_id")
    private WeeklyContestEntry entry;

    @ManyToOne(optional = false) @JoinColumn(name = "voter_user_id")
    private User voterUser;
}
