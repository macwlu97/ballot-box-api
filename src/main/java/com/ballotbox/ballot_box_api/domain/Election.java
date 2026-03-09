package com.ballotbox.ballot_box_api.domain;

import java.util.*;

/**
 * Election Aggregate. Manages voting logic within a specific instance.
 */
public record Election(UUID id, String title, List<String> candidates, Set<UUID> votersWhoVoted) {
    /**
     * Core business logic for casting a vote.
     * Ensures candidate validity and prevents double voting.
     */
    public void castVote(UUID voterId, String candidateName) {
        if (votersWhoVoted.contains(voterId)) {
            throw new DomainException("Voter has already cast a vote in this election.");
        }
        if (!candidates.contains(candidateName)) {
            throw new DomainException("Candidate " + candidateName + " does not exist in this election.");
        }
        votersWhoVoted.add(voterId);
    }
}