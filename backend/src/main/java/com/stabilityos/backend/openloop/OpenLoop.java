package com.stabilityos.backend.openloop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "open_loops")
public class OpenLoop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "input_item_id")
    private Long inputItemId;

    @Column(name = "cognitive_burden_id")
    private Long cognitiveBurdenId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "loop_type", nullable = false, length = 50)
    private String loopType;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "closure_condition", nullable = false, columnDefinition = "TEXT")
    private String closureCondition;

    @Column(name = "next_action", columnDefinition = "TEXT")
    private String nextAction;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "closure_note", columnDefinition = "TEXT")
    private String closureNote;

    public OpenLoop(
            Long inputItemId,
            Long cognitiveBurdenId,
            String title,
            String description,
            String loopType,
            String status,
            String closureCondition,
            String nextAction,
            LocalDate nextReviewDate
    ) {
        this.inputItemId = inputItemId;
        this.cognitiveBurdenId = cognitiveBurdenId;
        this.title = title;
        this.description = description;
        this.loopType = loopType;
        this.status = status;
        this.closureCondition = closureCondition;
        this.nextAction = nextAction;
        this.nextReviewDate = nextReviewDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void close(String note) {
        this.status = "closed";
        this.closedAt = LocalDateTime.now();
        this.closureNote = note;
        this.updatedAt = LocalDateTime.now();
    }

    public void park(String note) {
        this.status = "parked";
        this.closureNote = note;
        this.updatedAt = LocalDateTime.now();
    }
}