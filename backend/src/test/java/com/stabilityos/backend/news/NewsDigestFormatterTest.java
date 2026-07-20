package com.stabilityos.backend.news;

import com.stabilityos.backend.news.dto.DailyNewsDigestResponse;
import com.stabilityos.backend.news.dto.NewsItemResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NewsDigestFormatterTest {

    private final NewsDigestFormatter formatter = new NewsDigestFormatter();

    @Test
    void format_returnsNoNewsMessageWhenDigestIsEmpty() {
        DailyNewsDigestResponse digest = new DailyNewsDigestResponse(
                LocalDate.of(2026, 7, 12),
                0,
                Map.of(),
                List.of(),
                "No news items have been captured for 2026-07-12."
        );

        String result = formatter.format(digest);

        assertThat(result).isEqualTo("No news items have been captured for 2026-07-12.");
    }

    @Test
    void format_includesSummaryRegionsAndTopItems() {
        Map<String, Long> itemsByRegion = new LinkedHashMap<>();
        itemsByRegion.put("india", 1L);

        NewsItemResponse item = new NewsItemResponse(
                1L,
                "india",
                "Test headline",
                "Test summary",
                "Test Source",
                "https://example.com",
                4,
                LocalDate.of(2026, 7, 12),
                LocalDateTime.of(2026, 7, 12, 8, 30)
        );

        DailyNewsDigestResponse digest = new DailyNewsDigestResponse(
                LocalDate.of(2026, 7, 12),
                1,
                itemsByRegion,
                List.of(item),
                "Captured 1 news item."
        );

        String result = formatter.format(digest);

        assertThat(result).contains("News Digest for 2026-07-12");
        assertThat(result).contains("Captured 1 news item.");
        assertThat(result).contains("- india: 1");
        assertThat(result).contains("[india] Test headline");
        assertThat(result).contains("Test summary");
        assertThat(result).contains("Importance: 4");
        assertThat(result).contains("Source: Test Source");
        assertThat(result).contains("https://example.com");
    }
}