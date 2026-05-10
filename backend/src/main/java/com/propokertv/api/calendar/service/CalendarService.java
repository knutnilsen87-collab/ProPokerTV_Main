package com.propokertv.api.calendar.service;

import com.propokertv.api.calendar.domain.EventStatus;
import com.propokertv.api.calendar.domain.PokerEvent;
import com.propokertv.api.calendar.dto.CalendarDtos.PokerEventResponse;
import com.propokertv.api.calendar.repo.PokerEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final PokerEventRepository pokerEventRepository;

    @Transactional(readOnly = true)
    public List<PokerEventResponse> upcoming() {
        return pokerEventRepository
                .findByStatusAndStartsAtGreaterThanEqualOrderByFeaturedDescStartsAtAsc(EventStatus.PUBLISHED, Instant.now())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PokerEventResponse toResponse(PokerEvent event) {
        return new PokerEventResponse(
                event.getId(),
                event.getTitle(),
                event.getOrganizerName(),
                event.getOrganizerType(),
                event.getEventType(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getTimezone(),
                event.getLocationType(),
                event.getCountry(),
                event.getCity(),
                event.getVenueName(),
                event.getOnlineUrl(),
                event.getRegistrationUrl(),
                event.getAffiliateUrl(),
                event.isAffiliateDisclosureRequired(),
                event.getImageUrl(),
                event.getDescription(),
                fromCsv(event.getTagsCsv()),
                event.getStatus().name(),
                event.isFeatured(),
                event.isSponsored()
        );
    }

    private List<String> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
