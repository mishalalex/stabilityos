package com.stabilityos.backend.finance;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Expense() {
    }

    public Expense(BigDecimal amount, String category, String note, LocalDate entryDate) {
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.entryDate = entryDate;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}