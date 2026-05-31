package com.stabilityos.backend.persona.dto;

import java.util.List;

public record AssistantPersonaResponse(
        String mode,
        String tone,
        String responseStyle,
        List<String> operatingPrinciples
) {
}