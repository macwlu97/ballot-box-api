package com.ballotbox.ballot_box_api.api.exception;

import com.ballotbox.ballot_box_api.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global exception handler to map domain errors to HTTP statuses.
 */
@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handle(DomainException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", e.getMessage(),
                        "timestamp", System.currentTimeMillis()
                ));
    }
}