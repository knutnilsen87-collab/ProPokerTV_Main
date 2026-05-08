package com.propokertv.api.common.error;

import com.propokertv.api.common.api.ApiEnvelope;
import com.propokertv.api.common.api.ApiErrorBody;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiEnvelope<Void> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiErrorBody.FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toViolation)
                .toList();
        return ApiEnvelope.error(new ApiErrorBody(ErrorCode.VALIDATION_ERROR.name(), "Validation failed", violations));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiEnvelope<Void> handleConstraint(ConstraintViolationException ex) {
        var violations = ex.getConstraintViolations()
                .stream()
                .map(v -> new ApiErrorBody.FieldViolation(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return ApiEnvelope.error(new ApiErrorBody(ErrorCode.VALIDATION_ERROR.name(), "Validation failed", violations));
    }

    @ExceptionHandler(DomainException.class)
    public org.springframework.http.ResponseEntity<ApiEnvelope<Void>> handleDomain(DomainException ex) {
        return org.springframework.http.ResponseEntity.status(ex.getStatus())
                .body(ApiEnvelope.error(new ApiErrorBody(ex.getCode().name(), ex.getMessage(), null)));
    }

    @ExceptionHandler({BadCredentialsException.class})
    public org.springframework.http.ResponseEntity<ApiEnvelope<Void>> handleBadCredentials() {
        return org.springframework.http.ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiEnvelope.error(new ApiErrorBody(ErrorCode.UNAUTHORIZED.name(), "Invalid credentials", null)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public org.springframework.http.ResponseEntity<ApiEnvelope<Void>> handleAccessDenied() {
        return org.springframework.http.ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiEnvelope.error(new ApiErrorBody(ErrorCode.FORBIDDEN.name(), "Access denied", null)));
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ApiEnvelope<Void>> handleGeneric(Exception ex) {
        return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiEnvelope.error(new ApiErrorBody(ErrorCode.INTERNAL_ERROR.name(), "Unexpected server error", null)));
    }

    private ApiErrorBody.FieldViolation toViolation(FieldError error) {
        return new ApiErrorBody.FieldViolation(error.getField(), error.getDefaultMessage());
    }
}
