package com.propokertv.api.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propokertv.api.auth.service.JwtService;
import com.propokertv.api.support.IntegrationTestSupport;
import com.propokertv.api.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CalendarApiContractTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void publicCanListPublishedUpcomingEvents() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Nordic Poker Weekend"))
                .andExpect(jsonPath("$.data[0].eventType").value("Live tournament"))
                .andExpect(jsonPath("$.data[0].affiliateDisclosureRequired").value(true))
                .andExpect(jsonPath("$.data[0].status").value("PUBLISHED"));
    }

    @Test
    void publicCanTrackOutboundClickAndAdminCanPublishRemoveEvents() throws Exception {
        mockMvc.perform(post("/api/v1/calendar/events/1/outbound-click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sessionId":"smoke-session","targetUrlType":"affiliate","referrerPage":"/calendar"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Tracked."));

        String adminToken = tokenFor("admin@propokertv.test");
        String createResponse = mockMvc.perform(post("/api/v1/calendar/admin/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Admin Calendar Test",
                                  "organizerName":"ProPokerTV",
                                  "organizerType":"Platform",
                                  "eventType":"Watch party",
                                  "startsAt":"%s",
                                  "timezone":"Europe/Oslo",
                                  "locationType":"ONLINE",
                                  "registrationUrl":"https://example.com/admin-calendar-test",
                                  "description":"Safety-reviewed calendar event.",
                                  "tags":["test"],
                                  "status":"DRAFT",
                                  "featured":false,
                                  "sponsored":false
                                }
                                """.formatted(Instant.now().plusSeconds(86_400))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long eventId = objectMapper.readTree(createResponse).at("/data/id").asLong();

        mockMvc.perform(post("/api/v1/calendar/admin/events/{eventId}/publish", eventId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));

        mockMvc.perform(post("/api/v1/calendar/admin/events/{eventId}/remove", eventId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REMOVED"));
    }

    private String tokenFor(String email) {
        var user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        return jwtService.generateAccessToken(user);
    }
}
