package com.stabilityos.backend.burden.dto;

import java.time.LocalDateTime;

public record CognitiveBurdenResponse(
        Long id,
        Long inputItemId,
        String title,
        String description,
        String burdenType,
        String status,
        int burdenScore,
        String nextAction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt,
        String resolutionNote
) {
}