package com.propokertv.api.common.api;

import java.time.Instant;

public record ApiEnvelope<T>(
        boolean success,
        T data,
        ApiErrorBody error,
        Meta meta,
        Instant timestamp
) {
    public static <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(true, data, null, null, Instant.now());
    }

    public static <T> ApiEnvelope<T> ok(T data, Meta meta) {
        return new ApiEnvelope<>(true, data, null, meta, Instant.now());
    }

    public static <T> ApiEnvelope<T> error(ApiErrorBody error) {
        return new ApiEnvelope<>(false, null, error, null, Instant.now());
    }

    public record Meta(Integer page, Integer size, Long totalElements, Integer totalPages) {}
}
