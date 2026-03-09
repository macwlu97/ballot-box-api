package com.ballotbox.ballot_box_api.domain.model;

import com.ballotbox.ballot_box_api.domain.exception.DomainException;
import java.util.*;

/**
 * Domain record representing an Election instance.
 * Note: Uses mutable collections passed from the persistence layer for simplicity.
 */
public record Election(
        UUID id,
        String title,
        List<String> candidates,
        Set<UUID> votersWhoVoted,
        Map<String, Integer> results // Stores the vote count per candidate
) {
    /**
     * Executes the voting logic.
     * Validates voter eligibility and candidate existence before updating state.
     */
    public void castVote(UUID voterId, String candidateName) {
        if (votersWhoVoted.contains(voterId)) {
            throw new DomainException("Voter has already cast a vote in this election.");
        }
        if (!candidates.contains(candidateName)) {
            throw new DomainException("Candidate " + candidateName + " does not exist in this election.");
        }

        // Update the mutable collections linked to the Entity
        votersWhoVoted.add(voterId);
        results.merge(candidateName, 1, Integer::sum);
    }
}
