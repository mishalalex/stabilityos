package com.stabilityos.backend.news;

import com.stabilityos.backend.news.dto.CreateNewsItemRequest;
import com.stabilityos.backend.news.dto.DailyNewsDigestResponse;
import com.stabilityos.backend.news.dto.NewsItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/items")
    public NewsItemResponse createNewsItem(@Valid @RequestBody CreateNewsItemRequest request) {
        return newsService.createNewsItem(request);
    }

    @GetMapping("/items")
    public List<NewsItemResponse> listNewsItems() {
        return newsService.listNewsItems();
    }

    @GetMapping("/daily-digest")
    public DailyNewsDigestResponse dailyDigest(
            @RequestParam(required = false) LocalDate date
    ) {
        LocalDate digestDate = date == null ? LocalDate.now() : date;
        return newsService.getDailyDigest(digestDate);
    }
}