package com.propokertv.api.common.error;

import org.springframework.http.HttpStatus;

public class ConflictException extends DomainException {
    public ConflictException(ErrorCode code, String message) {
        super(code, HttpStatus.CONFLICT, message);
    }
}
