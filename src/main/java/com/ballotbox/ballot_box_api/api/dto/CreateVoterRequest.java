package com.ballotbox.ballot_box_api.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVoterRequest(@NotBlank String name) {}