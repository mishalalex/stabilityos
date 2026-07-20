package com.stabilityos.backend.commitment.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateCommitmentRequest(
        Long openLoopId,

        @NotBlank
        String title,

        String description,

        @NotBlank
        String commitmentType,

        @NotBlank
        String priority,

        LocalDate dueDate
) {
}