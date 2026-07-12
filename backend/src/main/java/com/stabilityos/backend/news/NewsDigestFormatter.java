package com.stabilityos.backend.news;

import com.stabilityos.backend.news.dto.DailyNewsDigestResponse;
import com.stabilityos.backend.news.dto.NewsItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NewsDigestFormatter {

    public String format(DailyNewsDigestResponse digest) {
        if (digest.itemCount() == 0) {
            return "No news items have been captured for " + digest.newsDate() + ".";
        }

        String regions = digest.itemsByRegion()
                .entrySet()
                .stream()
                .map(this::formatRegionCount)
                .collect(Collectors.joining("\n"));

        String topItems = digest.topItems()
                .stream()
                .map(this::formatNewsItem)
                .collect(Collectors.joining("\n\n"));

        return """
                News Digest for %s
                
                Summary:
                %s
                
                Regions:
                %s
                
                Top items:
                
                %s
                """.formatted(
                digest.newsDate(),
                digest.summary(),
                regions,
                topItems
        );
    }

    private String formatRegionCount(Map.Entry<String, Long> entry) {
        return "- " + entry.getKey() + ": " + entry.getValue();
    }

    private String formatNewsItem(NewsItemResponse item) {
        String source = item.sourceName() == null
                ? "Source: not specified"
                : "Source: " + item.sourceName();

        String sourceUrl = item.sourceUrl() == null
                ? ""
                : "\nLink: " + item.sourceUrl();

        return """
                - [%s] %s
                  %s
                  Importance: %d
                  %s%s
                """.formatted(
                item.region(),
                item.title(),
                item.summary(),
                item.importance(),
                source,
                sourceUrl
        ).stripTrailing();
    }
}