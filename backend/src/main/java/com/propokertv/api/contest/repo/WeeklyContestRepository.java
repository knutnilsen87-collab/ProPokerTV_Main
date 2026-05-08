package com.propokertv.api.contest.repo;

import com.propokertv.api.contest.domain.ContestStatus;
import com.propokertv.api.contest.domain.WeeklyContest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeeklyContestRepository extends JpaRepository<WeeklyContest, Long> {
    Optional<WeeklyContest> findFirstByStatusOrderByStartsAtDesc(ContestStatus status);
}
