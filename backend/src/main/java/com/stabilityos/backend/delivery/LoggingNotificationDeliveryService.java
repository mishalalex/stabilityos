package com.stabilityos.backend.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        prefix = "stabilityos.delivery.telegram",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class LoggingNotificationDeliveryService implements NotificationDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationDeliveryService.class);

    @Override
    public void send(String title, String message) {
        log.info("""
                
                Notification delivery fallback:
                
                {}
                
                {}
                """, title, message);
    }
}