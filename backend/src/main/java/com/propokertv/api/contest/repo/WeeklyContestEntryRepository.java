package com.propokertv.api.contest.repo;

import com.propokertv.api.contest.domain.WeeklyContestEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WeeklyContestEntryRepository extends JpaRepository<WeeklyContestEntry, Long> {
    List<WeeklyContestEntry> findByWeeklyContestId(Long weeklyContestId);
    boolean existsByWeeklyContestIdAndClipId(Long weeklyContestId, Long clipId);

    @Query("""
            select count(e)
            from WeeklyContestEntry e
            where e.clip.ownerUser.id = :userId
            """)
    long countNominationsForCreator(@Param("userId") Long userId);

    @Query("""
            select e.clip.categorySlug, count(e)
            from WeeklyContestEntry e
            where e.clip.ownerUser.id = :userId
            group by e.clip.categorySlug
            order by count(e) desc
            """)
    List<Object[]> topCategoriesForCreator(@Param("userId") Long userId);
}
