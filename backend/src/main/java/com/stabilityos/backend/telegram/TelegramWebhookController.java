package com.stabilityos.backend.telegram;

import com.stabilityos.backend.telegram.dto.TelegramUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telegram")
public class TelegramWebhookController {

    private static final String TELEGRAM_SECRET_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final String webhookSecret;
    private final TelegramInboundService telegramInboundService;

    public TelegramWebhookController(
            @Value("${stabilityos.telegram.webhook-secret}") String webhookSecret,
            TelegramInboundService telegramInboundService
    ) {
        this.webhookSecret = webhookSecret;
        this.telegramInboundService = telegramInboundService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveTelegramUpdate(
            @RequestHeader(value = TELEGRAM_SECRET_HEADER, required = false) String providedSecret,
            @RequestBody TelegramUpdateRequest update
    ) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.status(500).build();
        }

        if (providedSecret == null || !providedSecret.equals(webhookSecret)) {
            return ResponseEntity.status(401).build();
        }

        telegramInboundService.handleUpdate(update);
        return ResponseEntity.ok().build();
    }
}