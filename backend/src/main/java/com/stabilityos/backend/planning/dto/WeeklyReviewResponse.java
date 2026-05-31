package com.stabilityos.backend.planning.dto;

import java.math.BigDecimal;
import java.util.List;

public record WeeklyReviewResponse(
        BigDecimal totalSpentThisWeek,
        String topSpendingCategory,
        int expenseEntriesThisWeek,
        int healthEntriesThisWeek,
        BigDecimal averageSleepHours,
        BigDecimal averageWaterLiters,
        String summary,
        List<String> nextWeekPriorities
) {
}