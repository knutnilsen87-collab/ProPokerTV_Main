package com.propokertv.api.calendar;

import com.propokertv.api.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CalendarApiContractTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicCanListPublishedUpcomingEvents() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Nordic Poker Weekend"))
                .andExpect(jsonPath("$.data[0].eventType").value("Live tournament"))
                .andExpect(jsonPath("$.data[0].affiliateDisclosureRequired").value(true))
                .andExpect(jsonPath("$.data[0].status").value("PUBLISHED"));
    }
}
