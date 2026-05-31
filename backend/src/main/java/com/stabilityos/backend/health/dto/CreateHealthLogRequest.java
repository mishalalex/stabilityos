package com.stabilityos.backend.health.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateHealthLogRequest(
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "24.0")
        BigDecimal sleepHours,

        @DecimalMin(value = "0.0")
        BigDecimal waterLiters,

        @DecimalMin(value = "0.0")
        BigDecimal weightKg,

        String mood,

        String notes,

        @NotNull
        LocalDate entryDate
) {
}