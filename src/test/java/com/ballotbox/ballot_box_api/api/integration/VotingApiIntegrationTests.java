package com.ballotbox.ballot_box_api.api.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for the Ballot Box API.
 * Verifies the full integration between Web, Application, Domain, and Persistence layers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class VotingApiIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should successfully register a voter and cast a vote in an election")
    void shouldRegisterVoterAndCastVote() {
        // 1. Create a Voter
        CreateVoterRequest voterReq = new CreateVoterRequest("Alice Smith");
        ResponseEntity<Map> voterResponse = restTemplate.postForEntity("/api/voters", voterReq, Map.class);

        assertEquals(HttpStatus.OK, voterResponse.getStatusCode());
        UUID voterId = UUID.fromString(voterResponse.getBody().get("id").toString());
        assertNotNull(voterId);

        // 2. Create an Election
        CreateElectionRequest electionReq = new CreateElectionRequest(
                "Council Election 2025",
                List.of("Candidate Blue", "Candidate Red")
        );
        ResponseEntity<Map> electionResponse = restTemplate.postForEntity("/api/voters", electionReq, Map.class);

        assertEquals(HttpStatus.OK, electionResponse.getStatusCode());
        UUID electionId = UUID.fromString(electionResponse.getBody().get("id").toString());

        // 3. Cast a Vote
        VoteRequest voteReq = new VoteRequest(voterId, "Candidate Blue");
        ResponseEntity<Void> voteResponse = restTemplate.postForEntity(
                "/api/elections/" + electionId + "/votes",
                voteReq,
                Void.class
        );

        assertEquals(HttpStatus.OK, voteResponse.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when double voting occurs")
    void shouldReturnBadRequestForDoubleVoting() {
        // 1. Setup: Register Voter and Election
        UUID voterId = registerVoter("Bob Jones");
        UUID electionId = createElection("Quick Poll", List.of("Yes", "No"));

        // 2. First Vote (Success)
        restTemplate.postForEntity("/api/elections/" + electionId + "/votes", new VoteRequest(voterId, "Yes"), Void.class);

        // 3. Second Vote (Failure)
        ResponseEntity<Map> errorResponse = restTemplate.postForEntity(
                "/api/elections/" + electionId + "/votes",
                new VoteRequest(voterId, "No"),
                Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertEquals("Voter has already cast a vote in this election.", errorResponse.getBody().get("error"));
    }

    // Helper methods to keep tests clean
    private UUID registerVoter(String name) {
        return UUID.fromString(restTemplate.postForEntity("/api/voters", new CreateVoterRequest(name), Map.class)
                .getBody().get("id").toString());
    }

    private UUID createElection(String title, List<String> candidates) {
        return UUID.fromString(restTemplate.postForEntity("/api/elections", new CreateElectionRequest(title, candidates), Map.class)
                .getBody().get("id").toString());
    }
}
