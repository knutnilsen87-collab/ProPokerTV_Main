package com.propokertv.api.contest.repo;

import com.propokertv.api.contest.domain.ContestStatus;
import com.propokertv.api.contest.domain.WeeklyContest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface WeeklyContestRepository extends JpaRepository<WeeklyContest, Long> {
    Optional<WeeklyContest> findFirstByStatusOrderByStartsAtDesc(ContestStatus status);
    List<WeeklyContest> findByStatusOrderByFinalizedAtDesc(ContestStatus status);

    @Query("""
            select count(c)
            from WeeklyContest c
            where c.status = com.propokertv.api.contest.domain.ContestStatus.FINALIZED
              and c.winnerEntry.clip.ownerUser.id = :userId
            """)
    long countWinsForCreator(@Param("userId") Long userId);
}
