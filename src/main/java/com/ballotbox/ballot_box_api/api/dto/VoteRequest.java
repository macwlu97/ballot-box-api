package com.ballotbox.ballot_box_api.api.dto;

import jakarta.validation.constraints.NotBlank; // For @NotBlank
import jakarta.validation.constraints.NotNull; // For @NotNull
import java.util.UUID;                       // For UUID type

public record VoteRequest(@NotNull UUID voterId, @NotBlank String candidateName) {}