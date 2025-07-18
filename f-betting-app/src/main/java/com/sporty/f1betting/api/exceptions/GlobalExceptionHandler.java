package com.sporty.f1betting.api.exceptions;

import com.sporty.f1betting.domain.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.NoSuchElementException; // For cases where an entity is not found

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            "An unexpected error occurred: " + ex.getMessage(),
            request.getDescription(false)
        );

        System.err.println("Internal Server Error: " + ex.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

    // Handles specific business logic exceptions from BettingService
    @ExceptionHandler(BettingServiceException.class)
    public ResponseEntity<ErrorResponse> handleBettingServiceException(BettingServiceException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false)
        );
        System.err.println("Betting Service Error: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false)
        );
        System.err.println("Resource Not Found Error: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}
