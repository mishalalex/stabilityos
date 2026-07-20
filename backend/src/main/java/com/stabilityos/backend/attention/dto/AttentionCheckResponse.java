package com.stabilityos.backend.attention.dto;

import java.time.LocalDateTime;

public record AttentionCheckResponse(
        Long id,
        Long commitmentId,
        String source,
        String activityType,
        String title,
        String description,
        int urgencyScore,
        int importanceScore,
        String decision,
        String decisionReason,
        String recommendedAction,
        LocalDateTime createdAt
) {
}