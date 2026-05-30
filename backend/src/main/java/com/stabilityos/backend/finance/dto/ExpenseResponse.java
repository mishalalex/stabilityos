package com.stabilityos.backend.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        BigDecimal amount,
        String category,
        String note,
        LocalDate entryDate,
        LocalDateTime createdAt
) {
}