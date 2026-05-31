package com.stabilityos.backend.planning.dto;

public record EveningReflectionResponse(
        int expenseEntriesToday,
        int healthEntriesToday,
        String summary,
        String correctionForTomorrow
) {
}