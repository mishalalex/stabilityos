package com.stabilityos.backend.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(
        @NotBlank
        String message
) {
}