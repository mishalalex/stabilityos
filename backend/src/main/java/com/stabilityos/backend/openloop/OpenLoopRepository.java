package com.stabilityos.backend.openloop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OpenLoopRepository extends JpaRepository<OpenLoop, Long> {

    List<OpenLoop> findAllByOrderByCreatedAtDescIdDesc();

    List<OpenLoop> findByStatusOrderByCreatedAtDescIdDesc(String status);

    List<OpenLoop> findByStatusAndNextReviewDateLessThanEqualOrderByNextReviewDateAscIdAsc(
            String status,
            LocalDate nextReviewDate
    );
}