package com.propokertv.api.auth.api;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class CurrentUserController {
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiEnvelope<CurrentUser> me(CurrentUser currentUser) {
        return ApiEnvelope.ok(currentUser);
    }
}
