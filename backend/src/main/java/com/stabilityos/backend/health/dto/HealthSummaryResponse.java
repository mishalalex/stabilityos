package com.stabilityos.backend.health.dto;

import java.math.BigDecimal;

public record HealthSummaryResponse(
        int logCount,
        BigDecimal averageSleepHours,
        BigDecimal averageWaterLiters,
        BigDecimal latestWeightKg
) {
}