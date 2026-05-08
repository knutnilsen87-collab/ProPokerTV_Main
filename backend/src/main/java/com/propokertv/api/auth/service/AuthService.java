package com.propokertv.api.auth.service;

import com.propokertv.api.auth.domain.AuthActionTokenType;
import com.propokertv.api.auth.dto.AuthDtos.*;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.common.error.ConflictException;
import com.propokertv.api.common.error.ErrorCode;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.profile.domain.Profile;
import com.propokertv.api.profile.repo.ProfileRepository;
import com.propokertv.api.user.domain.Role;
import com.propokertv.api.user.domain.User;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthActionTokenService authActionTokenService;
    private final AnalyticsEventService analyticsEventService;
    private final com.propokertv.api.common.security.AppSecurityProperties securityProperties;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException(ErrorCode.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordHasher.hash(request.password()));
        user.setRole(Role.USER);
        var saved = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(saved);
        profile.setUsername("user" + saved.getId());
        profile.setDisplayName("Player " + saved.getId());
        profileRepository.save(profile);
        authActionTokenService.issue(saved, AuthActionTokenType.EMAIL_VERIFICATION,
                Duration.ofHours(securityProperties.getEmailVerificationTokenTtlHours()));
        analyticsEventService.track("creator_signed_up", Map.of("userId", saved.getId(), "role", saved.getRole().name()));

        return buildAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid credentials");
        }
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        var refreshToken = refreshTokenService.findActive(request.refreshToken());
        String accessToken = jwtService.generateAccessToken(refreshToken.getUser());
        String rotatedRefreshToken = refreshTokenService.rotate(request.refreshToken(), securityProperties.getRefreshTokenTtlDays());
        return new AuthResponse(
                refreshToken.getUser().getId(),
                refreshToken.getUser().getEmail(),
                refreshToken.getUser().getRole().name(),
                refreshToken.getUser().isEmailVerified(),
                new AuthTokens(accessToken, rotatedRefreshToken, "Bearer", securityProperties.getAccessTokenTtlMinutes() * 60)
        );
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email())
                .ifPresent(user -> authActionTokenService.issue(
                        user,
                        AuthActionTokenType.PASSWORD_RESET,
                        Duration.ofMinutes(securityProperties.getPasswordResetTokenTtlMinutes())
                ));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var token = authActionTokenService.consume(AuthActionTokenType.PASSWORD_RESET, request.token());
        var user = token.getUser();
        user.setPasswordHash(passwordHasher.hash(request.newPassword()));
        userRepository.save(user);
        refreshTokenService.revokeAllForUser(user.getId());
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        var token = authActionTokenService.consume(AuthActionTokenType.EMAIL_VERIFICATION, request.token());
        var user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.create(user, securityProperties.getRefreshTokenTtlDays());
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified(),
                new AuthTokens(accessToken, refreshToken, "Bearer", securityProperties.getAccessTokenTtlMinutes() * 60)
        );
    }
}
