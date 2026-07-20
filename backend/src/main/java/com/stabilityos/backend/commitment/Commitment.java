package com.stabilityos.backend.commitment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "commitments")
public class Commitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_loop_id")
    private Long openLoopId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "commitment_type", nullable = false, length = 50)
    private String commitmentType;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 50)
    private String priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "dropped_at")
    private LocalDateTime droppedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "outcome_note", columnDefinition = "TEXT")
    private String outcomeNote;

    public Commitment(
            Long openLoopId,
            String title,
            String description,
            String commitmentType,
            String status,
            String priority,
            LocalDate dueDate
    ) {
        this.openLoopId = openLoopId;
        this.title = title;
        this.description = description;
        this.commitmentType = commitmentType;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(String note) {
        this.status = "completed";
        this.completedAt = LocalDateTime.now();
        this.outcomeNote = note;
        this.updatedAt = LocalDateTime.now();
    }

    public void drop(String note) {
        this.status = "dropped";
        this.droppedAt = LocalDateTime.now();
        this.outcomeNote = note;
        this.updatedAt = LocalDateTime.now();
    }
}