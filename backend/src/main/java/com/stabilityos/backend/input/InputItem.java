package com.stabilityos.backend.input;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "input_items")
public class InputItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(name = "input_type", nullable = false, length = 50)
    private String inputType;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "telegram_message_id", length = 100)
    private String telegramMessageId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "detected_domain", length = 50)
    private String detectedDomain;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    protected InputItem() {
    }

    public InputItem(
            String source,
            String inputType,
            String rawText,
            String filePath,
            String telegramMessageId,
            String status,
            String detectedDomain,
            String errorMessage
    ) {
        this.source = source;
        this.inputType = inputType;
        this.rawText = rawText;
        this.filePath = filePath;
        this.telegramMessageId = telegramMessageId;
        this.status = status;
        this.detectedDomain = detectedDomain;
        this.errorMessage = errorMessage;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getInputType() {
        return inputType;
    }

    public String getRawText() {
        return rawText;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTelegramMessageId() {
        return telegramMessageId;
    }

    public String getStatus() {
        return status;
    }

    public String getDetectedDomain() {
        return detectedDomain;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}