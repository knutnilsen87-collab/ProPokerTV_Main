package com.propokertv.api.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propokertv.api.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileApiContractTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticatedUserCanUpsertAndReadProfile() throws Exception {
        String email = "profile+" + UUID.randomUUID() + "@propokertv.test";
        String username = "riverqueen" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String signupResponse = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"supersecret123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode signupJson = objectMapper.readTree(signupResponse);
        String accessToken = signupJson.at("/data/tokens/accessToken").asText();

        mockMvc.perform(put("/api/v1/profiles/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"%s",
                                  "displayName":"River Queen",
                                  "bio":"Aggressive late-stage grinder",
                                  "avatarUrl":"https://cdn.example.com/avatar.jpg",
                                  "bannerUrl":"https://cdn.example.com/banner.jpg"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.displayName").value("River Queen"));

        mockMvc.perform(get("/api/v1/profiles/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.bio").value("Aggressive late-stage grinder"));

        mockMvc.perform(get("/api/v1/profiles/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.displayName").value("River Queen"))
                .andExpect(jsonPath("$.data.avatarUrl").value("https://cdn.example.com/avatar.jpg"));
    }
}
