package com.stabilityos.backend.news;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {

    List<NewsItem> findAllByOrderByNewsDateDescImportanceDescIdDesc();

    List<NewsItem> findByNewsDateOrderByImportanceDescIdDesc(LocalDate newsDate);
}