package com.propokertv.api.calendar.dto;

import java.time.Instant;
import java.util.List;

public class CalendarDtos {
    public record PokerEventResponse(
            Long id,
            String title,
            String organizerName,
            String organizerType,
            String eventType,
            Instant startsAt,
            Instant endsAt,
            String timezone,
            String locationType,
            String country,
            String city,
            String venueName,
            String onlineUrl,
            String registrationUrl,
            String affiliateUrl,
            Boolean affiliateDisclosureRequired,
            String imageUrl,
            String description,
            List<String> tags,
            String status,
            Boolean featured,
            Boolean sponsored
    ) {}
}
