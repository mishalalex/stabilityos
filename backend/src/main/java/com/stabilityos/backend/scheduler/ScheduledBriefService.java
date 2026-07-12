package com.stabilityos.backend.scheduler;

import com.stabilityos.backend.assistant.AssistantService;
import com.stabilityos.backend.assistant.dto.AssistantResponse;
import com.stabilityos.backend.delivery.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@ConditionalOnProperty(prefix = "stabilityos.scheduler", name = "enabled", havingValue = "true")
public class ScheduledBriefService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledBriefService.class);

    private final AssistantService assistantService;
    private final NotificationDeliveryService notificationDeliveryService;

    @Scheduled(
            cron = "${stabilityos.scheduler.daily-brief-cron}",
            zone = "${stabilityos.scheduler.timezone}"
    )
    public void generateDailyBrief() {
        AssistantResponse response = assistantService.respond("What should I do today?");

        log.info("""
                
                Scheduled Stability Brief generated:
                
                {}
                """, response.reply());

        notificationDeliveryService.send("Today's Stability Brief", response.reply());
    }
}