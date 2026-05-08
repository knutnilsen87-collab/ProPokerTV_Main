package com.propokertv.api.auth.service;

public record SocialIdentity(
        String provider,
        String subject,
        String email,
        boolean emailVerified
) {}
