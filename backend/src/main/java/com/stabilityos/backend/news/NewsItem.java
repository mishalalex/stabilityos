package com.stabilityos.backend.news;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_items")
public class NewsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "source_name", length = 150)
    private String sourceName;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(nullable = false)
    private Integer importance;

    @Column(name = "news_date", nullable = false)
    private LocalDate newsDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected NewsItem() {
    }

    public NewsItem(
            String region,
            String title,
            String summary,
            String sourceName,
            String sourceUrl,
            Integer importance,
            LocalDate newsDate
    ) {
        this.region = region;
        this.title = title;
        this.summary = summary;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.importance = importance;
        this.newsDate = newsDate;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public Integer getImportance() {
        return importance;
    }

    public LocalDate getNewsDate() {
        return newsDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}