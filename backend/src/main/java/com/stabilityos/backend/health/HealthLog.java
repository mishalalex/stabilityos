package com.stabilityos.backend.health;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_logs")
public class HealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sleep_hours", precision = 4, scale = 2)
    private BigDecimal sleepHours;

    @Column(name = "water_liters", precision = 4, scale = 2)
    private BigDecimal waterLiters;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(length = 50)
    private String mood;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected HealthLog() {
    }

    public HealthLog(
            BigDecimal sleepHours,
            BigDecimal waterLiters,
            BigDecimal weightKg,
            String mood,
            String notes,
            LocalDate entryDate
    ) {
        this.sleepHours = sleepHours;
        this.waterLiters = waterLiters;
        this.weightKg = weightKg;
        this.mood = mood;
        this.notes = notes;
        this.entryDate = entryDate;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getSleepHours() {
        return sleepHours;
    }

    public BigDecimal getWaterLiters() {
        return waterLiters;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public String getMood() {
        return mood;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}