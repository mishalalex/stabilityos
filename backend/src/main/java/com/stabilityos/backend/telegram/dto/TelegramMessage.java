package com.stabilityos.backend.telegram.dto;

public record TelegramMessage(
        Long message_id,
        TelegramChat chat,
        TelegramUser from,
        String text
) {
}