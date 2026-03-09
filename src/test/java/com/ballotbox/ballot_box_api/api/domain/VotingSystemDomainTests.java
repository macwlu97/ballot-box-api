package com.ballotbox.ballot_box_api.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Domain Unit Tests (TDD) based on technical requirements.
 * Verifies:
 * 1. Voter management (blocking/unblocking).
 * 2. Election management (instances and options).
 * 3. Voting process (single vote constraint, valid candidate validation).
 */
public class VotingSystemDomainTests {

    // --- VOTER MANAGEMENT ---

    @Test
    @DisplayName("REQ: Voter Blocking - Should throw exception when a blocked voter attempts to validate eligibility")
    void shouldThrowExceptionWhenVoterIsBlocked() {
        // Given
        UUID voterId = UUID.randomUUID();
        Voter voter = new Voter(voterId, "John Doe", true);

        // When & Then
        DomainException exception = assertThrows(DomainException.class, voter::validateEligibility);
        assertTrue(exception.getMessage().contains("is blocked"));
    }

    @Test
    @DisplayName("REQ: Voter Unblocking - Should allow validation when voter is not blocked")
    void shouldAllowValidationForActiveVoter() {
        // Given
        Voter voter = new Voter(UUID.randomUUID(), "Jane Doe", false);

        // When & Then
        assertDoesNotThrow(voter::validateEligibility);
    }

    // --- ELECTION & VOTING ---

    @Test
    @DisplayName("REQ: Single Vote Constraint - Voter can only cast a vote once in a specific election instance")
    void shouldPreventDoubleVotingInSameInstance() {
        // Given
        UUID voterId = UUID.randomUUID();
        Election election = new Election(
                UUID.randomUUID(),
                "Mayor Election 2025",
                List.of("Candidate A", "Candidate B"),
                new HashSet<>()
        );

        // When
        election.castVote(voterId, "Candidate A");

        // Then
        DomainException exception = assertThrows(DomainException.class, () ->
                election.castVote(voterId, "Candidate B")
        );
        assertEquals("Voter has already cast a vote in this election.", exception.getMessage());
    }

    @Test
    @DisplayName("REQ: Instance Options - Each election instance has its own voting options (candidate validation)")
    void shouldPreventVotingForCandidateOutsideOfInstanceOptions() {
        // Given
        Election election = new Election(
                UUID.randomUUID(),
                "City Council",
                List.of("Candidate X"),
                new HashSet<>()
        );

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () ->
                election.castVote(UUID.randomUUID(), "Candidate from another election")
        );
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("REQ: Multiple Instances - Voting in one instance does not block voting in another instance for the same voter")
    void shouldAllowVotingInDifferentElectionInstances() {
        // Given
        UUID voterId = UUID.randomUUID();
        Election election1 = new Election(UUID.randomUUID(), "Election A", List.of("C1"), new HashSet<>());
        Election election2 = new Election(UUID.randomUUID(), "Election B", List.of("C2"), new HashSet<>());

        // When & Then
        assertDoesNotThrow(() -> election1.castVote(voterId, "C1"));
        assertDoesNotThrow(() -> election2.castVote(voterId, "C2"));

        assertTrue(election1.votersWhoVoted().contains(voterId));
        assertTrue(election2.votersWhoVoted().contains(voterId));
    }

    // --- DATA INTEGRITY ---

    @Test
    @DisplayName("LOGIC: Should correctly record the voter ID in the instance registry after a successful vote")
    void shouldRecordVoterIdAfterSuccessfulVote() {
        // Given
        UUID voterId = UUID.randomUUID();
        Election election = new Election(
                UUID.randomUUID(),
                "Referendum",
                List.of("YES", "NO"),
                new HashSet<>()
        );

        // When
        election.castVote(voterId, "YES");

        // Then
        assertEquals(1, election.votersWhoVoted().size());
        assertTrue(election.votersWhoVoted().contains(voterId));
    }
}