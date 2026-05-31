package com.stabilityos.backend.draft.dto;

import java.time.LocalDateTime;

public record ActionDraftResponse(
        Long id,
        Long inputItemId,
        String draftType,
        String title,
        String proposedAction,
        String status,
        LocalDateTime createdAt,
        LocalDateTime decidedAt,
        String decisionNote
) {
}