package com.propokertv.api.auth.service;

public interface SocialIdentityVerifier {
    SocialIdentity verify(String provider, String idToken);
}
