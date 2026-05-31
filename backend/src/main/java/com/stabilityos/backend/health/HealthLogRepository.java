package com.stabilityos.backend.health;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {
    List<HealthLog> findAllByOrderByEntryDateDescIdDesc();

    List<HealthLog> findByEntryDateGreaterThanEqualOrderByEntryDateDescIdDesc(LocalDate startDate);

    List<HealthLog> findByEntryDateBetweenOrderByEntryDateDescIdDesc(LocalDate startDate, LocalDate endDate);
}
