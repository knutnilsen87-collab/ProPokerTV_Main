package com.propokertv.api.auth.service;

import com.propokertv.api.auth.domain.AuthActionTokenType;
import com.propokertv.api.auth.domain.AuthIdentity;
import com.propokertv.api.auth.dto.AuthDtos.*;
import com.propokertv.api.auth.repo.AuthIdentityRepository;
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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthActionTokenService authActionTokenService;
    private final AuthIdentityRepository authIdentityRepository;
    private final SocialIdentityVerifier socialIdentityVerifier;
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

        createDefaultProfile(saved, "Player " + saved.getId());
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
    public AuthResponse socialLogin(SocialLoginRequest request) {
        SocialIdentity identity = socialIdentityVerifier.verify(request.provider(), request.idToken());
        var existingIdentity = authIdentityRepository.findByProviderAndProviderSubject(identity.provider(), identity.subject());
        if (existingIdentity.isPresent()) {
            return buildAuthResponse(existingIdentity.get().getUser());
        }

        User user = userRepository.findByEmailIgnoreCase(identity.email()).orElseGet(() -> createSocialUser(identity.email()));
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        AuthIdentity authIdentity = new AuthIdentity();
        authIdentity.setUser(user);
        authIdentity.setProvider(identity.provider());
        authIdentity.setProviderSubject(identity.subject());
        authIdentity.setEmail(identity.email());
        authIdentityRepository.save(authIdentity);
        analyticsEventService.track("social_auth_connected", Map.of(
                "userId", user.getId(),
                "provider", identity.provider()
        ));
        return buildAuthResponse(user);
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

    private User createSocialUser(String email) {
        User user = new User();
        user.setEmail(email.toLowerCase(Locale.ROOT));
        user.setPasswordHash(passwordHasher.hash(UUID.randomUUID().toString() + UUID.randomUUID()));
        user.setRole(Role.USER);
        user.setEmailVerified(true);
        User saved = userRepository.save(user);
        createDefaultProfile(saved, displayNameFromEmail(email));
        analyticsEventService.track("creator_signed_up", Map.of(
                "userId", saved.getId(),
                "role", saved.getRole().name(),
                "source", "social"
        ));
        return saved;
    }

    private void createDefaultProfile(User user, String displayName) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setUsername("user" + user.getId());
        profile.setDisplayName(displayName);
        profileRepository.save(profile);
    }

    private String displayNameFromEmail(String email) {
        String localPart = email.split("@", 2)[0].replaceAll("[._-]+", " ").trim();
        if (localPart.isBlank()) {
            return "Player";
        }
        return localPart.length() > 80 ? localPart.substring(0, 80) : localPart;
    }
}
