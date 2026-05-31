package com.stabilityos.backend.draft;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_drafts")
public class ActionDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "input_item_id")
    private Long inputItemId;

    @Column(name = "draft_type", nullable = false, length = 50)
    private String draftType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "proposed_action", nullable = false, columnDefinition = "TEXT")
    private String proposedAction;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "decision_note", columnDefinition = "TEXT")
    private String decisionNote;

    protected ActionDraft() {
    }

    public ActionDraft(
            Long inputItemId,
            String draftType,
            String title,
            String proposedAction,
            String status
    ) {
        this.inputItemId = inputItemId;
        this.draftType = draftType;
        this.title = title;
        this.proposedAction = proposedAction;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public void confirm(String note) {
        this.status = "confirmed";
        this.decidedAt = LocalDateTime.now();
        this.decisionNote = note;
    }

    public void reject(String note) {
        this.status = "rejected";
        this.decidedAt = LocalDateTime.now();
        this.decisionNote = note;
    }

    public Long getId() {
        return id;
    }

    public Long getInputItemId() {
        return inputItemId;
    }

    public String getDraftType() {
        return draftType;
    }

    public String getTitle() {
        return title;
    }

    public String getProposedAction() {
        return proposedAction;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public String getDecisionNote() {
        return decisionNote;
    }
}