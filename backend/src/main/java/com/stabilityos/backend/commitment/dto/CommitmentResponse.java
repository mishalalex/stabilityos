package com.stabilityos.backend.commitment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CommitmentResponse(
        Long id,
        Long openLoopId,
        String title,
        String description,
        String commitmentType,
        String status,
        String priority,
        LocalDate dueDate,
        LocalDateTime completedAt,
        LocalDateTime droppedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String outcomeNote
) {
}