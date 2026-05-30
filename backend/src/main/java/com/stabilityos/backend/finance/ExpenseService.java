package com.stabilityos.backend.finance;

import com.stabilityos.backend.finance.dto.CreateExpenseRequest;
import com.stabilityos.backend.finance.dto.ExpenseResponse;
import com.stabilityos.backend.finance.dto.MonthlySummaryResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        Expense expense = new Expense(
                request.amount(),
                request.category().trim().toLowerCase(),
                request.note(),
                request.entryDate()
        );

        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    public List<ExpenseResponse> listExpenses() {
        return expenseRepository.findAllByOrderByEntryDateDescIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MonthlySummaryResponse getMonthlySummary() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);

        List<Expense> expenses = expenseRepository
                .findByEntryDateGreaterThanEqualOrderByEntryDateDescIdDesc(startOfMonth);

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());

            byCategory.merge(
                    expense.getCategory(),
                    expense.getAmount(),
                    BigDecimal::add
            );
        }

        return new MonthlySummaryResponse(
                total,
                expenses.size(),
                byCategory
        );
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getNote(),
                expense.getEntryDate(),
                expense.getCreatedAt()
        );
    }
}