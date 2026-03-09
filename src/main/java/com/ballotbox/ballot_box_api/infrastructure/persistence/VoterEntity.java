package com.ballotbox.ballot_box_api.infrastructure.persistence;

import com.ballotbox.ballot_box_api.domain.model.Voter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "voters")
@Getter @Setter
@NoArgsConstructor
public class VoterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean blocked;

    /**
     * Maps the database entity to a clean domain record.
     */
    public Voter toDomain() {
        // Maps 'blocked' from DB to 'isBlocked' in the Record
        return new Voter(id, name, blocked);
    }

    public static VoterEntity fromDomain(Voter voter) {
        VoterEntity entity = new VoterEntity();
        entity.setId(voter.id());
        entity.setName(voter.name());
        entity.setBlocked(voter.isBlocked());
        return entity;
    }

}
