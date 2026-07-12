package com.stabilityos.backend.openloop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpenLoopResponse(
        Long id,
        Long inputItemId,
        Long cognitiveBurdenId,
        String title,
        String description,
        String loopType,
        String status,
        String closureCondition,
        String nextAction,
        LocalDate nextReviewDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        String closureNote
) {
}