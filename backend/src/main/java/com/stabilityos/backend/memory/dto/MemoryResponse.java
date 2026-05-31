package com.stabilityos.backend.memory.dto;

public record MemoryResponse(
        String key,
        String value,
        String type,
        int importance
) {
}