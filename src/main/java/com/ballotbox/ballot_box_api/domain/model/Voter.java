package com.ballotbox.ballot_box_api.domain.model;

import com.ballotbox.ballot_box_api.domain.exception.DomainException;

import java.util.*;

/**
 * Voter Aggregate. Uses Java Record for built-in immutability.
 */
public record Voter(UUID id, String name, boolean isBlocked) {
    /**
     * Business rule: A blocked voter cannot participate in any election.
     */
    public void validateEligibility() {
        if (isBlocked) {
            throw new DomainException("Voter is blocked and cannot cast a vote.");
        }
    }
}