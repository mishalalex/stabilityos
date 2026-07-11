package com.stabilityos.backend.burden;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cognitive_burdens")
public class CognitiveBurden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "input_item_id")
    private Long inputItemId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "burden_type", nullable = false, length = 50)
    private String burdenType;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "burden_score", nullable = false)
    private int burdenScore;

    @Column(name = "next_action", columnDefinition = "TEXT")
    private String nextAction;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    public CognitiveBurden(
            Long inputItemId,
            String title,
            String description,
            String burdenType,
            String status,
            int burdenScore,
            String nextAction
    ) {
        this.inputItemId = inputItemId;
        this.title = title;
        this.description = description;
        this.burdenType = burdenType;
        this.status = status;
        this.burdenScore = burdenScore;
        this.nextAction = nextAction;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void close(String note) {
        this.status = "closed";
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNote = note;
        this.updatedAt = LocalDateTime.now();
    }

    public void park(String note) {
        this.status = "parked";
        this.resolutionNote = note;
        this.updatedAt = LocalDateTime.now();
    }
}