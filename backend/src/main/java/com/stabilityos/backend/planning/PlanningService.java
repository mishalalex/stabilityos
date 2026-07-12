package com.stabilityos.backend.planning;

import com.stabilityos.backend.finance.Expense;
import com.stabilityos.backend.finance.ExpenseRepository;
import com.stabilityos.backend.health.HealthLog;
import com.stabilityos.backend.health.HealthLogRepository;
import com.stabilityos.backend.planning.dto.DailyBriefResponse;
import com.stabilityos.backend.planning.dto.EveningReflectionResponse;
import com.stabilityos.backend.planning.dto.WeeklyReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PlanningService {

    private final ExpenseRepository expenseRepository;
    private final HealthLogRepository healthLogRepository;

    public DailyBriefResponse getDailyBrief() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        List<Expense> weekExpenses = expenseRepository
                .findByEntryDateGreaterThanEqualOrderByEntryDateDescIdDesc(weekStart);

        List<HealthLog> weekHealthLogs = healthLogRepository
                .findByEntryDateGreaterThanEqualOrderByEntryDateDescIdDesc(weekStart);

        String financeAction = weekExpenses.isEmpty()
                ? "Log every expense today. The system is blind without spending data."
                : "Keep expense logging current today. Do not wait until night.";

        BigDecimal averageSleep = average(
                weekHealthLogs.stream()
                        .map(HealthLog::getSleepHours)
                        .filter(Objects::nonNull)
                        .toList()
        );

        BigDecimal averageWater = average(
                weekHealthLogs.stream()
                        .map(HealthLog::getWaterLiters)
                        .filter(Objects::nonNull)
                        .toList()
        );

        String healthAction = "Log sleep, water, weight, and mood today.";

        if (averageSleep != null && averageSleep.compareTo(BigDecimal.valueOf(6.5)) < 0) {
            healthAction = "Sleep is below target. Prioritize an earlier night.";
        } else if (averageWater != null && averageWater.compareTo(BigDecimal.valueOf(2.0)) < 0) {
            healthAction = "Water intake is low. Cross 2 liters before evening.";
        }

        return new DailyBriefResponse(
                financeAction,
                healthAction,
                "Do one focused 45-minute backend/Spring Boot session today.",
                "Avoid drifting into YouTube or scrolling during low-work periods."
        );
    }

    public EveningReflectionResponse getEveningReflection() {
        LocalDate today = LocalDate.now();

        int expenseCount = expenseRepository
                .findByEntryDateBetweenOrderByEntryDateDescIdDesc(today, today)
                .size();

        int healthCount = healthLogRepository
                .findByEntryDateBetweenOrderByEntryDateDescIdDesc(today, today)
                .size();

        String summary;
        String correction;

        if (expenseCount == 0 && healthCount == 0) {
            summary = "You did not log expenses or health data today.";
            correction = "Tomorrow, log at least one expense and one health entry before evening.";
        } else if (expenseCount > 0 && healthCount == 0) {
            summary = "You logged expenses today, but missed health logging.";
            correction = "Tomorrow, keep expense logging and add sleep/water/weight logging.";
        } else if (expenseCount == 0) {
            summary = "You logged health data today, but missed expense logging.";
            correction = "Tomorrow, log spending immediately after it happens.";
        } else {
            summary = "You logged both expenses and health data today.";
            correction = "Tomorrow, maintain the same discipline.";
        }

        return new EveningReflectionResponse(
                expenseCount,
                healthCount,
                summary,
                correction
        );
    }

    public WeeklyReviewResponse getWeeklyReview() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        List<Expense> expenses = expenseRepository
                .findByEntryDateBetweenOrderByEntryDateDescIdDesc(weekStart, today);

        List<HealthLog> healthLogs = healthLogRepository
                .findByEntryDateBetweenOrderByEntryDateDescIdDesc(weekStart, today);

        BigDecimal totalSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            categoryTotals.merge(
                    expense.getCategory(),
                    expense.getAmount(),
                    BigDecimal::add
            );
        }

        String topCategory = categoryTotals.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        BigDecimal averageSleep = average(
                healthLogs.stream()
                        .map(HealthLog::getSleepHours)
                        .filter(Objects::nonNull)
                        .toList()
        );

        BigDecimal averageWater = average(
                healthLogs.stream()
                        .map(HealthLog::getWaterLiters)
                        .filter(Objects::nonNull)
                        .toList()
        );

        List<String> priorities = new ArrayList<>();

        if (expenses.isEmpty()) {
            priorities.add("Log expenses daily next week.");
        } else if (topCategory != null) {
            priorities.add("Review and control spending in " + topCategory + ".");
        }

        if (healthLogs.isEmpty()) {
            priorities.add("Log health data daily next week.");
        } else {
            if (averageSleep != null && averageSleep.compareTo(BigDecimal.valueOf(6.5)) < 0) {
                priorities.add("Improve sleep consistency next week.");
            }
            if (averageWater != null && averageWater.compareTo(BigDecimal.valueOf(2.0)) < 0) {
                priorities.add("Increase daily water intake next week.");
            }
        }

        if (priorities.size() < 3) {
            priorities.add("Complete at least three focused backend learning sessions.");
        }

        if (priorities.size() < 3) {
            priorities.add("Keep daily logging consistent instead of backfilling later.");
        }

        if (priorities.size() < 3) {
            priorities.add("Review the weekly summary before starting next week.");
        }

        String expenseText = expenses.size() == 1 ? "1 expense entry" : expenses.size() + " expense entries";
        String healthText = healthLogs.size() == 1 ? "1 health entry" : healthLogs.size() + " health entries";

        String summary = "This week had " + expenseText + " and " + healthText + ".";

        return new WeeklyReviewResponse(
                totalSpent,
                topCategory,
                expenses.size(),
                healthLogs.size(),
                averageSleep,
                averageWater,
                summary,
                priorities.stream().limit(3).toList()
        );
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) {
            return null;
        }

        BigDecimal total = values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }
}