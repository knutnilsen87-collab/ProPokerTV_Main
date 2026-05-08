package com.propokertv.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record SignupRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 10, max = 72) String password
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {}

    public record ForgotPasswordRequest(
            @Email @NotBlank String email
    ) {}

    public record ResetPasswordRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 10, max = 72) String newPassword
    ) {}

    public record VerifyEmailRequest(
            @NotBlank String token
    ) {}

    public record AuthTokens(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresInSeconds
    ) {}

    public record AuthResponse(
            Long userId,
            String email,
            String role,
            boolean emailVerified,
            AuthTokens tokens
    ) {}
}
