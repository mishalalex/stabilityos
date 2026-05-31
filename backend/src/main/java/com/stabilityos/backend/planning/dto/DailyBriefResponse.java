package com.stabilityos.backend.planning.dto;

public record DailyBriefResponse(
        String financeAction,
        String healthAction,
        String workAction,
        String watchOut
) {
}