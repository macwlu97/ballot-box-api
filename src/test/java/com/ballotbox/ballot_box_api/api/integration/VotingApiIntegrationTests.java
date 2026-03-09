package com.ballotbox.ballot_box_api.api.integration;

import com.ballotbox.ballot_box_api.api.dto.CreateElectionRequest;
import com.ballotbox.ballot_box_api.api.dto.CreateVoterRequest;
import com.ballotbox.ballot_box_api.api.dto.VoteRequest;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class VotingApiIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterVoterAndCastVote() {
        // 1. Create a Voter
        CreateVoterRequest voterReq = new CreateVoterRequest("Alice Smith");
        // Kontroler zwraca Map.of("id", ...), więc odbieramy jako Map
        ResponseEntity<Map> voterRes = restTemplate.postForEntity("/api/voters", voterReq, Map.class);

        assertThat(voterRes.getStatusCode()).isEqualTo(HttpStatus.OK); // Zmienione z CREATED na OK
        assertThat(voterRes.getBody()).isNotNull();
        UUID voterId = UUID.fromString(voterRes.getBody().get("id").toString());

        // 2. Create an Election
        CreateElectionRequest electReq = new CreateElectionRequest(
                "General Election 2026",
                List.of("Candidate A", "Candidate B")
        );
        ResponseEntity<Map> electRes = restTemplate.postForEntity("/api/elections", electReq, Map.class);

        assertThat(electRes.getStatusCode()).isEqualTo(HttpStatus.OK); // Zmienione z CREATED na OK
        assertThat(electRes.getBody()).isNotNull();
        UUID electionId = UUID.fromString(electRes.getBody().get("id").toString());

        // 3. Cast a Vote
        VoteRequest voteReq = new VoteRequest(voterId, "Candidate A");
        ResponseEntity<Void> voteRes = restTemplate.postForEntity(
                "/api/elections/" + electionId + "/votes",
                voteReq,
                Void.class
        );

        assertThat(voteRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
