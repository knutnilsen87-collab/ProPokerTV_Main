package com.propokertv.api.contest;

import com.fasterxml.jackson.databind.JsonNode;
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

class ContestLifecycleContractTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void adminCanRunContestLifecycleAndFinalizedContestLocksVotes() throws Exception {
        String adminToken = tokenFor("admin@propokertv.test");
        String fanToken = tokenFor("fan@propokertv.test");

        Instant startsAt = Instant.now().minusSeconds(60);
        Instant endsAt = Instant.now().plusSeconds(86_400);

        String createResponse = mockMvc.perform(post("/api/v1/contests")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Lifecycle Test Contest","startsAt":"%s","endsAt":"%s"}
                                """.formatted(startsAt, endsAt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long contestId = objectMapper.readTree(createResponse).at("/data/id").asLong();

        mockMvc.perform(post("/api/v1/contests/{contestId}/entries", contestId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clipId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.entries[0].clipId").value(1));

        mockMvc.perform(post("/api/v1/contests/{contestId}/open", contestId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OPEN"));

        String openResponse = mockMvc.perform(get("/api/v1/contests/open"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode openJson = objectMapper.readTree(openResponse);
        long entryId = openJson.at("/data/entries/0/entryId").asLong();

        mockMvc.perform(post("/api/v1/contests/{contestId}/vote", contestId)
                        .header("Authorization", "Bearer " + fanToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"entryId":%d}
                                """.formatted(entryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.entries[0].votes").value(1));

        mockMvc.perform(post("/api/v1/contests/{contestId}/vote", contestId)
                        .header("Authorization", "Bearer " + fanToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"entryId":%d}
                                """.formatted(entryId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("ALREADY_VOTED"));

        mockMvc.perform(post("/api/v1/contests/{contestId}/finalize", contestId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("FINALIZED"))
                .andExpect(jsonPath("$.data.winnerEntryId").value(entryId))
                .andExpect(jsonPath("$.data.winnerClipId").value(1))
                .andExpect(jsonPath("$.data.winnerCreatorUserId").value(2));

        mockMvc.perform(post("/api/v1/contests/{contestId}/vote", contestId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"entryId":%d}
                                """.formatted(entryId)))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/contests/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].winnerClipId").value(1));

        mockMvc.perform(get("/api/v1/creators/acecreator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reputation.badges[0]").value("Clip of the Week Winner"));

        mockMvc.perform(get("/api/v1/leaderboards/top-creators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].creatorSlug").value("acecreator"));
    }

    private String tokenFor(String email) {
        var user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        return jwtService.generateAccessToken(user);
    }
}
