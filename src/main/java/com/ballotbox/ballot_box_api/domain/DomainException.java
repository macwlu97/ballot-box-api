package com.ballotbox.ballot_box_api.domain;

/**
 * Custom exception for business rule violations.
 */
class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
}