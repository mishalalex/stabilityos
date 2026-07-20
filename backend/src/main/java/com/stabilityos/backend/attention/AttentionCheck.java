package com.stabilityos.backend.attention;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "attention_checks")
public class AttentionCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commitment_id")
    private Long commitmentId;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "urgency_score", nullable = false)
    private int urgencyScore;

    @Column(name = "importance_score", nullable = false)
    private int importanceScore;

    @Column(nullable = false, length = 50)
    private String decision;

    @Column(name = "decision_reason", nullable = false, columnDefinition = "TEXT")
    private String decisionReason;

    @Column(name = "recommended_action", nullable = false, columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AttentionCheck(
            Long commitmentId,
            String source,
            String activityType,
            String title,
            String description,
            int urgencyScore,
            int importanceScore,
            String decision,
            String decisionReason,
            String recommendedAction
    ) {
        this.commitmentId = commitmentId;
        this.source = source;
        this.activityType = activityType;
        this.title = title;
        this.description = description;
        this.urgencyScore = urgencyScore;
        this.importanceScore = importanceScore;
        this.decision = decision;
        this.decisionReason = decisionReason;
        this.recommendedAction = recommendedAction;
        this.createdAt = LocalDateTime.now();
    }
}