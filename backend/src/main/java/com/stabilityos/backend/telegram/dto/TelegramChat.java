package com.stabilityos.backend.telegram.dto;

public record TelegramChat(
        Long id,
        String type,
        String first_name,
        String username
) {
}