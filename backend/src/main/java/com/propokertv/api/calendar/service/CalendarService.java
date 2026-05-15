package com.propokertv.api.calendar.service;

import com.propokertv.api.calendar.domain.EventStatus;
import com.propokertv.api.calendar.domain.EventOutboundClick;
import com.propokertv.api.calendar.domain.PokerEvent;
import com.propokertv.api.calendar.dto.CalendarDtos.EventClickRequest;
import com.propokertv.api.calendar.dto.CalendarDtos.PokerEventResponse;
import com.propokertv.api.calendar.dto.CalendarDtos.UpsertPokerEventRequest;
import com.propokertv.api.calendar.repo.EventOutboundClickRepository;
import com.propokertv.api.calendar.repo.PokerEventRepository;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final PokerEventRepository pokerEventRepository;
    private final EventOutboundClickRepository clickRepository;
    private final UserRepository userRepository;
    private final AnalyticsEventService analyticsEventService;

    @Transactional(readOnly = true)
    public List<PokerEventResponse> upcoming() {
        return pokerEventRepository
                .findByStatusAndStartsAtGreaterThanEqualOrderByFeaturedDescStartsAtAsc(EventStatus.PUBLISHED, Instant.now())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PokerEventResponse create(UpsertPokerEventRequest request) {
        PokerEvent event = new PokerEvent();
        apply(event, request);
        PokerEvent saved = pokerEventRepository.save(event);
        analyticsEventService.track("event_created", Map.of("eventId", saved.getId()));
        return toResponse(saved);
    }

    @Transactional
    public PokerEventResponse update(Long eventId, UpsertPokerEventRequest request) {
        PokerEvent event = pokerEventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        apply(event, request);
        PokerEvent saved = pokerEventRepository.save(event);
        analyticsEventService.track("event_updated", Map.of("eventId", saved.getId(), "status", saved.getStatus().name()));
        return toResponse(saved);
    }

    @Transactional
    public PokerEventResponse publish(Long eventId) {
        PokerEvent event = pokerEventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        event.setStatus(EventStatus.PUBLISHED);
        PokerEvent saved = pokerEventRepository.save(event);
        analyticsEventService.track("event_published", Map.of("eventId", saved.getId()));
        return toResponse(saved);
    }

    @Transactional
    public PokerEventResponse remove(Long eventId) {
        PokerEvent event = pokerEventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        event.setStatus(EventStatus.REMOVED);
        PokerEvent saved = pokerEventRepository.save(event);
        analyticsEventService.track("event_removed", Map.of("eventId", saved.getId()));
        return toResponse(saved);
    }

    @Transactional
    public void trackClick(Long eventId, Long userId, EventClickRequest request) {
        PokerEvent event = pokerEventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        EventOutboundClick click = new EventOutboundClick();
        click.setEvent(event);
        if (userId != null) {
            userRepository.findById(userId).ifPresent(click::setUser);
        }
        click.setSessionId(request.sessionId());
        click.setTargetUrlType(request.targetUrlType() == null ? "official" : request.targetUrlType());
        click.setReferrerPage(request.referrerPage());
        clickRepository.save(click);
        analyticsEventService.track("event_outbound_click", Map.of("eventId", eventId, "targetUrlType", click.getTargetUrlType()));
    }

    private void apply(PokerEvent event, UpsertPokerEventRequest request) {
        event.setTitle(request.title());
        event.setOrganizerName(request.organizerName());
        event.setOrganizerType(request.organizerType());
        event.setEventType(request.eventType());
        event.setStartsAt(request.startsAt());
        event.setEndsAt(request.endsAt());
        event.setTimezone(request.timezone());
        event.setLocationType(request.locationType());
        event.setCountry(request.country());
        event.setCity(request.city());
        event.setVenueName(request.venueName());
        event.setOnlineUrl(request.onlineUrl());
        event.setRegistrationUrl(request.registrationUrl());
        event.setAffiliateUrl(request.affiliateUrl());
        event.setAffiliateDisclosureRequired(Boolean.TRUE.equals(request.affiliateDisclosureRequired()));
        event.setImageUrl(request.imageUrl());
        event.setDescription(request.description());
        event.setTagsCsv(toCsv(request.tags()));
        event.setStatus(request.status() == null ? EventStatus.DRAFT : EventStatus.valueOf(request.status()));
        event.setFeatured(Boolean.TRUE.equals(request.featured()));
        event.setSponsored(Boolean.TRUE.equals(request.sponsored()));
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

    private String toCsv(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(",", values.stream().filter(value -> value != null && !value.isBlank()).map(String::trim).toList());
    }
}
