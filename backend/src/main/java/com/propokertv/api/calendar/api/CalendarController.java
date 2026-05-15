package com.propokertv.api.calendar.api;

import com.propokertv.api.calendar.dto.CalendarDtos.PokerEventResponse;
import com.propokertv.api.calendar.dto.CalendarDtos.EventClickRequest;
import com.propokertv.api.calendar.dto.CalendarDtos.UpsertPokerEventRequest;
import com.propokertv.api.calendar.service.CalendarService;
import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping("/events")
    public ApiEnvelope<List<PokerEventResponse>> upcomingEvents() {
        return ApiEnvelope.ok(calendarService.upcoming());
    }

    @PostMapping("/events/{eventId}/outbound-click")
    public ApiEnvelope<String> trackOutboundClick(@PathVariable Long eventId, @RequestBody EventClickRequest request, CurrentUser currentUser) {
        calendarService.trackClick(eventId, currentUser == null ? null : currentUser.userId(), request);
        return ApiEnvelope.ok("Tracked.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PostMapping("/admin/events")
    public ApiEnvelope<PokerEventResponse> createEvent(@RequestBody @Valid UpsertPokerEventRequest request) {
        return ApiEnvelope.ok(calendarService.create(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PutMapping("/admin/events/{eventId}")
    public ApiEnvelope<PokerEventResponse> updateEvent(@PathVariable Long eventId, @RequestBody @Valid UpsertPokerEventRequest request) {
        return ApiEnvelope.ok(calendarService.update(eventId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PostMapping("/admin/events/{eventId}/publish")
    public ApiEnvelope<PokerEventResponse> publishEvent(@PathVariable Long eventId) {
        return ApiEnvelope.ok(calendarService.publish(eventId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PostMapping("/admin/events/{eventId}/remove")
    public ApiEnvelope<PokerEventResponse> removeEvent(@PathVariable Long eventId) {
        return ApiEnvelope.ok(calendarService.remove(eventId));
    }
}
