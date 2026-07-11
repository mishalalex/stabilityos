package com.stabilityos.backend.burden.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateCognitiveBurdenRequest(
        Long inputItemId,

        @NotBlank
        String title,

        String description,

        @NotBlank
        String burdenType,

        @Min(1)
        @Max(5)
        int burdenScore,

        String nextAction
) {
}