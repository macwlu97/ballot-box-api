package com.ballotbox.ballot_box_api.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateElectionRequest(@NotBlank String title, @NotEmpty List<String> candidates) {}