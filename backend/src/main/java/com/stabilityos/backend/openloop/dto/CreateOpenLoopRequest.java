package com.stabilityos.backend.openloop.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateOpenLoopRequest(
        Long inputItemId,
        Long cognitiveBurdenId,

        @NotBlank
        String title,

        String description,

        @NotBlank
        String loopType,

        @NotBlank
        String closureCondition,

        String nextAction,

        LocalDate nextReviewDate
) {
}