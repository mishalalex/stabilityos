package com.stabilityos.backend.telegram.dto;

public record TelegramUser(
        Long id,
        Boolean is_bot,
        String first_name,
        String username
) {
}