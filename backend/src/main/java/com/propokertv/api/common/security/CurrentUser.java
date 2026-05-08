package com.propokertv.api.common.security;

public record CurrentUser(
        Long userId,
        String email,
        String role
) {}
