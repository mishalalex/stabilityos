package com.stabilityos.backend.finance.dto;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlySummaryResponse(
        BigDecimal totalSpent,
        int expenseCount,
        Map<String, BigDecimal> byCategory
) {
}