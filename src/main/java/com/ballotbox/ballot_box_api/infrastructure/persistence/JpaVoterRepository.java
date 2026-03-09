package com.ballotbox.ballot_box_api.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository; // Core JPA repository interface
import org.springframework.stereotype.Repository;            // Stereotype for persistence layer
import java.util.UUID;                                       // ID type

@Repository
public interface JpaVoterRepository extends JpaRepository<VoterEntity, UUID> {}