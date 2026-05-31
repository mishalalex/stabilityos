package com.stabilityos.backend.input.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateInputItemRequest(
        @NotBlank
        String source,

        @NotBlank
        String inputType,

        String rawText,

        String filePath,

        String telegramMessageId
) {
}