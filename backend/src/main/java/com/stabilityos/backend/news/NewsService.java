package com.stabilityos.backend.news;

import com.stabilityos.backend.news.dto.CreateNewsItemRequest;
import com.stabilityos.backend.news.dto.DailyNewsDigestResponse;
import com.stabilityos.backend.news.dto.NewsItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsItemRepository newsItemRepository;

    public NewsItemResponse createNewsItem(CreateNewsItemRequest request) {
        NewsItem newsItem = new NewsItem(
                normalize(request.region()),
                request.title().trim(),
                request.summary().trim(),
                normalizeNullable(request.sourceName()),
                normalizeNullable(request.sourceUrl()),
                request.importance(),
                request.newsDate()
        );

        NewsItem saved = newsItemRepository.save(newsItem);
        return toResponse(saved);
    }

    public List<NewsItemResponse> listNewsItems() {
        return newsItemRepository.findAllByOrderByNewsDateDescImportanceDescIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DailyNewsDigestResponse getDailyDigest(LocalDate newsDate) {
        List<NewsItem> items = newsItemRepository.findByNewsDateOrderByImportanceDescIdDesc(newsDate);

        Map<String, Long> itemsByRegion = items.stream()
                .collect(Collectors.groupingBy(
                        NewsItem::getRegion,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        List<NewsItemResponse> topItems = items.stream()
                .limit(5)
                .map(this::toResponse)
                .toList();

        String summary = buildSummary(newsDate, items);

        return new DailyNewsDigestResponse(
                newsDate,
                items.size(),
                itemsByRegion,
                topItems,
                summary
        );
    }

    private String buildSummary(LocalDate newsDate, List<NewsItem> items) {
        if (items.isEmpty()) {
            return "No news items have been captured for " + newsDate + ".";
        }

        String mostImportantTitle = items.getFirst().getTitle();

        return "Captured " + items.size()
                + " news item(s) for " + newsDate
                + ". Top item: " + mostImportantTitle + ".";
    }

    private NewsItemResponse toResponse(NewsItem newsItem) {
        return new NewsItemResponse(
                newsItem.getId(),
                newsItem.getRegion(),
                newsItem.getTitle(),
                newsItem.getSummary(),
                newsItem.getSourceName(),
                newsItem.getSourceUrl(),
                newsItem.getImportance(),
                newsItem.getNewsDate(),
                newsItem.getCreatedAt()
        );
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}