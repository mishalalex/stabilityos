package com.stabilityos.backend.finance;

import com.stabilityos.backend.finance.dto.CreateExpenseRequest;
import com.stabilityos.backend.finance.dto.ExpenseResponse;
import com.stabilityos.backend.finance.dto.MonthlySummaryResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses")
    public ExpenseResponse createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.createExpense(request);
    }

    @GetMapping("/expenses")
    public List<ExpenseResponse> listExpenses() {
        return expenseService.listExpenses();
    }

    @GetMapping("/finance/monthly-summary")
    public MonthlySummaryResponse monthlySummary() {
        return expenseService.getMonthlySummary();
    }
}