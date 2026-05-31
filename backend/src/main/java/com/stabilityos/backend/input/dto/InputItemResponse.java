package com.stabilityos.backend.input.dto;

import java.time.LocalDateTime;

public record InputItemResponse(
        Long id,
        String source,
        String inputType,
        String rawText,
        String filePath,
        String telegramMessageId,
        String status,
        String detectedDomain,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime processedAt
) {
}