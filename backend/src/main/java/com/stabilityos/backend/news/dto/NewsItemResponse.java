package com.stabilityos.backend.news.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NewsItemResponse(
        Long id,
        String region,
        String title,
        String summary,
        String sourceName,
        String sourceUrl,
        Integer importance,
        LocalDate newsDate,
        LocalDateTime createdAt
) {
}