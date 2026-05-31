package com.stabilityos.backend.telegram.dto;

public record TelegramUpdateRequest(
        Long update_id,
        TelegramMessage message
) {
}