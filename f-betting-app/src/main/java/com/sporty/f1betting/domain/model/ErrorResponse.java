package com.sporty.f1betting.domain.model;

import java.time.Instant;
import org.springframework.http.HttpStatus;

// A record to represent a standardized error response
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public ErrorResponse(HttpStatus httpStatus, String message, String path) {
        this(Instant.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path);
    }
}
