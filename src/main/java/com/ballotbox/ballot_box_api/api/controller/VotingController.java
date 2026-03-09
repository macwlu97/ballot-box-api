package com.ballotbox.ballot_box_api.api.controller;

import com.ballotbox.ballot_box_api.api.dto.CreateElectionRequest;
import com.ballotbox.ballot_box_api.api.dto.CreateVoterRequest;
import com.ballotbox.ballot_box_api.api.dto.VoteRequest;
import com.ballotbox.ballot_box_api.domain.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class VotingController {
    private final VotingService service;

    public VotingController(VotingService service) {
        this.service = service;
    }

    @PostMapping("/voters")
    public ResponseEntity<Map<String, UUID>> registerVoter(@RequestBody @Valid CreateVoterRequest req) {
        return ResponseEntity.ok(Map.of("id", service.registerVoter(req.name())));
    }

    @PatchMapping("/voters/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable UUID id) {
        service.setBlockStatus(id, true);
    }

    @PatchMapping("/voters/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable UUID id) {
        service.setBlockStatus(id, false);
    }

    @PostMapping("/elections")
    public ResponseEntity<Map<String, UUID>> createElection(@RequestBody @Valid CreateElectionRequest req) {
        return ResponseEntity.ok(Map.of("id", service.createElection(req.title(), req.candidates())));
    }

    @PostMapping("/elections/{id}/votes")
    @ResponseStatus(HttpStatus.OK)
    public void vote(@PathVariable UUID id, @RequestBody @Valid VoteRequest req) {
        service.castVote(id, req.voterId(), req.candidateName());
    }
}