package com.propokertv.api.contest.repo;

import com.propokertv.api.contest.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByWeeklyContestIdAndVoterUserId(Long weeklyContestId, Long voterUserId);
    long countByEntryId(Long entryId);

    @Query("""
            select count(v)
            from Vote v
            where v.entry.clip.ownerUser.id = :userId
            """)
    long countVotesForCreator(@Param("userId") Long userId);
}
