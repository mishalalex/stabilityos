package com.stabilityos.backend.scheduler;

import com.stabilityos.backend.delivery.NotificationDeliveryService;
import com.stabilityos.backend.news.NewsDigestFormatter;
import com.stabilityos.backend.news.NewsService;
import com.stabilityos.backend.news.dto.DailyNewsDigestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@ConditionalOnProperty(prefix = "stabilityos.scheduler", name = "news-digest-enabled", havingValue = "true")
public class ScheduledNewsDigestService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledNewsDigestService.class);

    private final NewsService newsService;
    private final NewsDigestFormatter newsDigestFormatter;
    private final NotificationDeliveryService notificationDeliveryService;

    public ScheduledNewsDigestService(
            NewsService newsService,
            NewsDigestFormatter newsDigestFormatter,
            NotificationDeliveryService notificationDeliveryService
    ) {
        this.newsService = newsService;
        this.newsDigestFormatter = newsDigestFormatter;
        this.notificationDeliveryService = notificationDeliveryService;
    }

    @Scheduled(
            cron = "${stabilityos.scheduler.news-digest-cron}",
            zone = "${stabilityos.scheduler.timezone}"
    )
    public void sendDailyNewsDigest() {
        LocalDate today = LocalDate.now();

        DailyNewsDigestResponse digest = newsService.getDailyDigest(today);
        String message = newsDigestFormatter.format(digest);

        log.info("""
                
                Scheduled News Digest generated:
                
                {}
                """, message);

        if (digest.itemCount() == 0) {
            log.info("Skipping Telegram delivery because no news items were captured for {}.", today);
            return;
        }

        notificationDeliveryService.send("Today's News Digest", message);
    }
}