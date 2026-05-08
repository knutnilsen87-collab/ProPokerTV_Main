package com.propokertv.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propokertv.api.auth.domain.AuthActionTokenType;
import com.propokertv.api.auth.repo.AuthActionTokenRepository;
import com.propokertv.api.auth.repo.RefreshTokenRepository;
import com.propokertv.api.support.IntegrationTestSupport;
import com.propokertv.api.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
class AuthApiContractTest extends IntegrationTestSupport {
    private static final Pattern TOKEN_LOG_PATTERN = Pattern.compile(
            "LOCAL_AUTH_TOKEN type=(\\w+) userId=(\\d+) email=([^\\s]+) token=([^\\s\\\"]+)"
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthActionTokenRepository authActionTokenRepository;

    @Test
    void signupLoginRefreshAndVerifyFlowWorks(CapturedOutput output) throws Exception {
        String email = "flow+" + UUID.randomUUID() + "@propokertv.test";
        String signupResponse = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"supersecret123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.emailVerified").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode signupJson = objectMapper.readTree(signupResponse);
        String originalRefreshToken = signupJson.at("/data/tokens/refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"supersecret123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email));

        String verifyToken = extractLatestToken(output.getOut(), AuthActionTokenType.EMAIL_VERIFICATION, email);
        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s"}
                                """.formatted(verifyToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Email verification completed."));

        assertThat(userRepository.findByEmailIgnoreCase(email))
                .get()
                .extracting(user -> user.isEmailVerified())
                .isEqualTo(true);

        String refreshResponse = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(originalRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emailVerified").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String rotatedRefreshToken = objectMapper.readTree(refreshResponse).at("/data/tokens/refreshToken").asText();
        assertThat(rotatedRefreshToken).isNotEqualTo(originalRefreshToken);
        assertThat(refreshTokenRepository.findByUserIdAndRevokedAtIsNull(
                userRepository.findByEmailIgnoreCase(email).orElseThrow().getId()
        )).extracting("tokenHash").hasSizeGreaterThanOrEqualTo(2);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(originalRefreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
    }

    @Test
    void forgotAndResetPasswordFlowWorks(CapturedOutput output) throws Exception {
        String email = "reset+" + UUID.randomUUID() + "@propokertv.test";
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"supersecret123"}
                                """.formatted(email)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("If the email exists, a reset instruction will be sent."));

        String resetToken = extractLatestToken(output.getOut(), AuthActionTokenType.PASSWORD_RESET, email);
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","newPassword":"newersecret123"}
                                """.formatted(resetToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Password reset completed."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"supersecret123"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"newersecret123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email));

        assertThat(authActionTokenRepository.findAll())
                .anyMatch(token -> token.getTokenType() == AuthActionTokenType.PASSWORD_RESET && token.getConsumedAt() != null);
    }

    private String extractLatestToken(String output, AuthActionTokenType tokenType, String email) {
        String matchedToken = null;
        Matcher matcher = TOKEN_LOG_PATTERN.matcher(output);
        while (matcher.find()) {
            if (matcher.group(1).equals(tokenType.name()) && matcher.group(3).equals(email)) {
                matchedToken = matcher.group(4);
            }
        }
        assertThat(matchedToken).as("logged %s token for %s", tokenType, email).isNotBlank();
        return matchedToken;
    }
}
