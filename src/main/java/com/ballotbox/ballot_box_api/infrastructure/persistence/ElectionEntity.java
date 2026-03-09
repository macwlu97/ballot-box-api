package com.ballotbox.ballot_box_api.infrastructure.persistence;

import com.ballotbox.ballot_box_api.domain.model.Election;
import jakarta.persistence.*; // Includes @Entity, @Table, @Id, @GeneratedValue, @Column, @ElementCollection, etc.
import lombok.Getter;           // Lombok for getters
import lombok.NoArgsConstructor; // Lombok for empty constructor
import lombok.Setter;           // Lombok for setters

import java.util.ArrayList;     // Required for new ArrayList<>()
import java.util.HashSet;       // Required for new HashSet<>()
import java.util.List;          // Required for List type
import java.util.Set;           // Required for Set type
import java.util.UUID;          // Required for UUID type

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
    @Column(name = "candidate_name")
    private List<String> candidates = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "election_votes", joinColumns = @JoinColumn(name = "election_id"))
    @Column(name = "voter_id")
    private Set<UUID> votedVoterIds = new HashSet<>();

    /**
     * Maps the database entity to a clean domain aggregate.
     */
    public Election toDomain() {
        return new Election(id, title, candidates, new HashSet<>(votedVoterIds));
    }
}