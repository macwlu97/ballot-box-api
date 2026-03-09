package com.ballotbox.ballot_box_api.infrastructure;

import com.ballotbox.ballot_box_api.domain.exception.DomainException; // Twój błąd biznesowy
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for mapping domain and validation errors to HTTP responses.
 * "Problem Details" patterns.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles business rule violations (e.g., voter blocked, double voting).
     * Maps DomainException to 400 Bad Request.
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setType(URI.create("https://ballotbox.com/errors/business-rule-violation"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Handles validation errors from @Valid annotations in DTOs.
     * Maps MethodArgumentNotValidException to 400 Bad Request with field details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request content");
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://ballotbox.com/errors/validation-error"));
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}