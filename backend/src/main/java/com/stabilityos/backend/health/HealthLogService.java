package com.stabilityos.backend.health;

import com.stabilityos.backend.health.dto.CreateHealthLogRequest;
import com.stabilityos.backend.health.dto.HealthLogResponse;
import com.stabilityos.backend.health.dto.HealthSummaryResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class HealthLogService {

    private final HealthLogRepository healthLogRepository;

    public HealthLogService(HealthLogRepository healthLogRepository) {
        this.healthLogRepository = healthLogRepository;
    }

    public HealthLogResponse createHealthLog(CreateHealthLogRequest request) {
        HealthLog healthLog = new HealthLog(
                request.sleepHours(),
                request.waterLiters(),
                request.weightKg(),
                request.mood(),
                request.notes(),
                request.entryDate()
        );

        HealthLog saved = healthLogRepository.save(healthLog);
        return toResponse(saved);
    }

    public List<HealthLogResponse> listHealthLogs() {
        return healthLogRepository.findAllByOrderByEntryDateDescIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public HealthSummaryResponse getHealthSummary() {
        LocalDate startDate = LocalDate.now().minusDays(6);

        List<HealthLog> logs = healthLogRepository
                .findByEntryDateGreaterThanEqualOrderByEntryDateDescIdDesc(startDate);

        BigDecimal averageSleep = average(
                logs.stream()
                        .map(HealthLog::getSleepHours)
                        .filter(Objects::nonNull)
                        .toList()
        );

        BigDecimal averageWater = average(
                logs.stream()
                        .map(HealthLog::getWaterLiters)
                        .filter(Objects::nonNull)
                        .toList()
        );

        BigDecimal latestWeight = logs.stream()
                .map(HealthLog::getWeightKg)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return new HealthSummaryResponse(
                logs.size(),
                averageSleep,
                averageWater,
                latestWeight
        );
    }

    private HealthLogResponse toResponse(HealthLog healthLog) {
        return new HealthLogResponse(
                healthLog.getId(),
                healthLog.getSleepHours(),
                healthLog.getWaterLiters(),
                healthLog.getWeightKg(),
                healthLog.getMood(),
                healthLog.getNotes(),
                healthLog.getEntryDate(),
                healthLog.getCreatedAt()
        );
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) {
            return null;
        }

        BigDecimal total = values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(
                BigDecimal.valueOf(values.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase();
    }
}