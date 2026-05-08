package com.propokertv.api.auth.api;

import com.propokertv.api.auth.dto.AuthDtos.*;
import com.propokertv.api.auth.service.AuthService;
import com.propokertv.api.common.api.ApiEnvelope;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiEnvelope<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ApiEnvelope.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ApiEnvelope<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiEnvelope.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiEnvelope<AuthResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        return ApiEnvelope.ok(authService.refresh(request));
    }

    @PostMapping("/forgot-password")
    public ApiEnvelope<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.requestPasswordReset(request);
        return ApiEnvelope.ok("If the email exists, a reset instruction will be sent.");
    }

    @PostMapping("/reset-password")
    public ApiEnvelope<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiEnvelope.ok("Password reset completed.");
    }

    @PostMapping("/verify-email")
    public ApiEnvelope<String> verifyEmail(@RequestBody @Valid VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ApiEnvelope.ok("Email verification completed.");
    }
}
