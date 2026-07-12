package com.stabilityos.backend.memory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "assistant_memory")
public class AssistantMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memory_key", nullable = false, unique = true, length = 150)
    private String key;

    @Column(name = "memory_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "memory_type", nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private Integer importance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AssistantMemory(String key, String value, String type, Integer importance) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.importance = importance;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}