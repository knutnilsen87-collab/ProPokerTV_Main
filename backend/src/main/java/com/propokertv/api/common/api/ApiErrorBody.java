package com.propokertv.api.common.api;

import java.util.List;

public record ApiErrorBody(
        String code,
        String message,
        List<FieldViolation> fieldViolations
) {
    public record FieldViolation(String field, String message) {}
}
