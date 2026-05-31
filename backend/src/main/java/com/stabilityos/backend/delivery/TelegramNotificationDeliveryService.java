package com.stabilityos.backend.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@ConditionalOnProperty(
        prefix = "stabilityos.delivery.telegram",
        name = "enabled",
        havingValue = "true"
)
public class TelegramNotificationDeliveryService implements NotificationDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationDeliveryService.class);

    private final RestClient restClient;
    private final String botToken;
    private final String chatId;

    public TelegramNotificationDeliveryService(
            @Value("${stabilityos.delivery.telegram.bot-token}") String botToken,
            @Value("${stabilityos.delivery.telegram.chat-id}") String chatId
    ) {
        this.restClient = RestClient.create("https://api.telegram.org");
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public void send(String title, String message) {
        if (botToken == null || botToken.isBlank()) {
            log.error("Telegram delivery is enabled, but bot token is missing.");
            return;
        }

        if (chatId == null || chatId.isBlank()) {
            log.error("Telegram delivery is enabled, but chat id is missing.");
            return;
        }

        String telegramMessage = """
                *%s*
                
                %s
                """.formatted(escapeMarkdown(title), escapeMarkdown(message));

        try {
            restClient.post()
                    .uri("/bot{token}/sendMessage", botToken)
                    .body(Map.of(
                            "chat_id", chatId,
                            "text", telegramMessage,
                            "parse_mode", "MarkdownV2"
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Telegram notification delivered successfully.");
        } catch (Exception exception) {
            log.error("Failed to deliver Telegram notification.", exception);
        }
    }

    private String escapeMarkdown(String input) {
        if (input == null) {
            return "";
        }

        return input
                .replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}