package com.stabilityos.backend.scheduler;

import com.stabilityos.backend.assistant.AssistantService;
import com.stabilityos.backend.assistant.dto.AssistantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "stabilityos.scheduler", name = "enabled", havingValue = "true")
public class ScheduledBriefService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledBriefService.class);

    private final AssistantService assistantService;

    public ScheduledBriefService(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

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
    }
}