package com.stabilityos.backend.news.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DailyNewsDigestResponse(
        LocalDate newsDate,
        int itemCount,
        Map<String, Long> itemsByRegion,
        List<NewsItemResponse> topItems,
        String summary
) {
}