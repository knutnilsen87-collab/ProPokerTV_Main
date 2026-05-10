package com.propokertv.api.calendar.repo;

import com.propokertv.api.calendar.domain.EventStatus;
import com.propokertv.api.calendar.domain.PokerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface PokerEventRepository extends JpaRepository<PokerEvent, Long> {
    List<PokerEvent> findByStatusAndStartsAtGreaterThanEqualOrderByFeaturedDescStartsAtAsc(EventStatus status, Instant startsAt);
}
