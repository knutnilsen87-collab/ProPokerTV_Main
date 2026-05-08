package com.propokertv.api.contest.repo;

import com.propokertv.api.contest.domain.WeeklyContestEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeeklyContestEntryRepository extends JpaRepository<WeeklyContestEntry, Long> {
    List<WeeklyContestEntry> findByWeeklyContestId(Long weeklyContestId);
}
