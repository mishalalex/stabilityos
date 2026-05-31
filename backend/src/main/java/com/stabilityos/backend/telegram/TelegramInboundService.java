package com.stabilityos.backend.telegram;

import com.stabilityos.backend.delivery.NotificationDeliveryService;
import com.stabilityos.backend.input.InputItemService;
import com.stabilityos.backend.input.dto.CreateInputItemRequest;
import com.stabilityos.backend.input.dto.InputItemResponse;
import com.stabilityos.backend.telegram.dto.TelegramMessage;
import com.stabilityos.backend.telegram.dto.TelegramUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramInboundService {

    private static final Logger log = LoggerFactory.getLogger(TelegramInboundService.class);

    private final InputItemService inputItemService;
    private final NotificationDeliveryService notificationDeliveryService;

    public TelegramInboundService(
            InputItemService inputItemService,
            NotificationDeliveryService notificationDeliveryService
    ) {
        this.inputItemService = inputItemService;
        this.notificationDeliveryService = notificationDeliveryService;
    }

    public void handleUpdate(TelegramUpdateRequest update) {
        if (update == null || update.message() == null) {
            log.info("Ignoring Telegram update without message.");
            return;
        }

        TelegramMessage message = update.message();

        if (message.text() == null || message.text().isBlank()) {
            log.info("Ignoring Telegram message without text. messageId={}", message.message_id());
            return;
        }

        InputItemResponse saved = inputItemService.createInputItem(
                new CreateInputItemRequest(
                        "telegram",
                        "text",
                        message.text(),
                        null,
                        message.message_id() == null ? null : message.message_id().toString()
                )
        );

        log.info(
                "Stored Telegram text input. inputItemId={}, detectedDomain={}",
                saved.id(),
                saved.detectedDomain()
        );

        notificationDeliveryService.send(
                "Input received",
                "I have captured your message and stored it in the input inbox.\n\nDetected domain: "
                        + saved.detectedDomain()
        );
    }
}