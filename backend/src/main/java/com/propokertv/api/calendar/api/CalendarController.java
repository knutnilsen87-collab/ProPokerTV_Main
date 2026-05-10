package com.propokertv.api.calendar.api;

import com.propokertv.api.calendar.dto.CalendarDtos.PokerEventResponse;
import com.propokertv.api.calendar.service.CalendarService;
import com.propokertv.api.common.api.ApiEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
