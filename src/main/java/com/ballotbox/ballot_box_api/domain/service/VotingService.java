package com.ballotbox.ballot_box_api.domain.service;

import com.ballotbox.ballot_box_api.domain.exception.DomainException;
import com.ballotbox.ballot_box_api.domain.model.Election;
import com.ballotbox.ballot_box_api.domain.model.Voter;
import com.ballotbox.ballot_box_api.infrastructure.persistence.ElectionEntity;
import com.ballotbox.ballot_box_api.infrastructure.persistence.JpaElectionRepository;
import com.ballotbox.ballot_box_api.infrastructure.persistence.JpaVoterRepository;
import com.ballotbox.ballot_box_api.infrastructure.persistence.VoterEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VotingService {
    private final JpaVoterRepository voterRepo;
    private final JpaElectionRepository electionRepo;

    @Transactional
    public UUID registerVoter(String name) {
        VoterEntity v = new VoterEntity();
        v.setName(name);
        v.setBlocked(false);
        return voterRepo.save(v).getId();
    }

    @Transactional
    public void setBlockStatus(UUID voterId, boolean blocked) {
        VoterEntity v = voterRepo.findById(voterId)
                .orElseThrow(() -> new DomainException("Voter not found."));
        v.setBlocked(blocked);
    }

    @Transactional
    public UUID createElection(String title, List<String> candidates) {
        ElectionEntity e = new ElectionEntity();
        e.setTitle(title);
        e.setCandidates(candidates);
        return electionRepo.save(e).getId();
    }

    @Transactional
    public void castVote(UUID electionId, UUID voterId, String candidate) {
        // Load entities
        VoterEntity vEntity = voterRepo.findById(voterId)
                .orElseThrow(() -> new DomainException("Voter not found."));
        ElectionEntity eEntity = electionRepo.findById(electionId)
                .orElseThrow(() -> new DomainException("Election not found."));

        // Map to domain to perform business logic
        Voter voter = vEntity.toDomain();
        Election election = eEntity.toDomain();

        // Execution of domain rules
        voter.validateEligibility();
        election.castVote(voterId, candidate);

        // Synchronize state back to the persistence layer
        eEntity.setVotedVoterIds(election.votersWhoVoted());
    }
}