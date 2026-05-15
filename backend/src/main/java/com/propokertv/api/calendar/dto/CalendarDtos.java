package com.propokertv.api.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public class CalendarDtos {
    public record UpsertPokerEventRequest(
            @NotBlank @Size(max = 140)
            String title,
            @NotBlank @Size(max = 140)
            String organizerName,
            @NotBlank @Size(max = 60)
            String organizerType,
            @NotBlank @Size(max = 60)
            String eventType,
            @NotNull
            Instant startsAt,
            Instant endsAt,
            @NotBlank @Size(max = 80)
            String timezone,
            @NotBlank @Size(max = 30)
            String locationType,
            @Size(max = 80)
            String country,
            @Size(max = 80)
            String city,
            @Size(max = 140)
            String venueName,
            @Size(max = 500)
            String onlineUrl,
            @Size(max = 500)
            String registrationUrl,
            @Size(max = 500)
            String affiliateUrl,
            Boolean affiliateDisclosureRequired,
            @Size(max = 500)
            String imageUrl,
            @Size(max = 1000)
            String description,
            @Size(max = 20)
            List<String> tags,
            String status,
            Boolean featured,
            Boolean sponsored
    ) {}

    public record EventClickRequest(
            String sessionId,
            String targetUrlType,
            String referrerPage
    ) {}

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
