package com.ballotbox.ballot_box_api.infrastructure.persistence;

import com.ballotbox.ballot_box_api.domain.model.Election;
import jakarta.persistence.*; // Includes @Entity, @Table, @Id, @GeneratedValue, @Column, @ElementCollection, etc.
import lombok.Getter;           // Lombok for getters
import lombok.NoArgsConstructor; // Lombok for empty constructor
import lombok.Setter;           // Lombok for setters

import java.util.*;

@Entity
@Table(name = "elections")
@Getter @Setter
@NoArgsConstructor
public class ElectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "election_candidates", joinColumns = @JoinColumn(name = "election_id"))
    private List<String> candidates = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "election_voters", joinColumns = @JoinColumn(name = "election_id"))
    private Set<UUID> votedVoterIds = new HashSet<>();

    /**
     * Stores vote counts.
     * MapKey: Candidate Name, Value: Number of votes.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "election_results", joinColumns = @JoinColumn(name = "election_id"))
    @MapKeyColumn(name = "candidate_name")
    @Column(name = "vote_count")
    private Map<String, Integer> results = new HashMap<>();

    /**
     * Converts the entity to a domain record.
     * We pass the actual collection references so the domain logic can modify them.
     */
    public Election toDomain() {
        return new Election(id, title, candidates, votedVoterIds, results);
    }
}


