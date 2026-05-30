package com.stabilityos.backend.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByEntryDateGreaterThanEqualOrderByEntryDateDescIdDesc(LocalDate startDate);

    List<Expense> findAllByOrderByEntryDateDescIdDesc();
}