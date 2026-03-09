package com.ballotbox.ballot_box_api.domain.exception;

/**
 * Custom exception for business rule violations.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
}