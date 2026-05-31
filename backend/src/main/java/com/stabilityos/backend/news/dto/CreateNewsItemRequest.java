package com.stabilityos.backend.news.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateNewsItemRequest(
        @NotBlank
        String region,

        @NotBlank
        String title,

        @NotBlank
        String summary,

        String sourceName,

        String sourceUrl,

        @NotNull
        @Min(1)
        @Max(5)
        Integer importance,

        @NotNull
        LocalDate newsDate
) {
}