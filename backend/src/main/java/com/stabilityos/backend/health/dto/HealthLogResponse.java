package com.stabilityos.backend.health.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record HealthLogResponse(
        Long id,
        BigDecimal sleepHours,
        BigDecimal waterLiters,
        BigDecimal weightKg,
        String mood,
        String notes,
        LocalDate entryDate,
        LocalDateTime createdAt
) {
}
