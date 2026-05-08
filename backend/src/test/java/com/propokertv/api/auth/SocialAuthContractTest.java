package com.propokertv.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propokertv.api.auth.service.SocialIdentity;
import com.propokertv.api.auth.service.SocialIdentityVerifier;
import com.propokertv.api.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SocialAuthContractTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SocialIdentityVerifier socialIdentityVerifier;

    @Test
    void socialLoginCreatesUserThenReturnsSameUserForProviderIdentity() throws Exception {
        String email = "social-" + UUID.randomUUID() + "@propokertv.test";
        when(socialIdentityVerifier.verify(eq("google"), eq("valid-token")))
                .thenReturn(new SocialIdentity("google", "google-subject-123", email, true));

        String firstResponse = mockMvc.perform(post("/api/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"provider\":\"google\",\"idToken\":\"valid-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.emailVerified").value(true))
                .andExpect(jsonPath("$.data.tokens.accessToken").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long userId = objectMapper.readTree(firstResponse).at("/data/userId").asLong();
        String accessToken = objectMapper.readTree(firstResponse).at("/data/tokens/accessToken").asText();

        mockMvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId));

        mockMvc.perform(post("/api/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"provider\":\"google\",\"idToken\":\"valid-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId));
    }
}
