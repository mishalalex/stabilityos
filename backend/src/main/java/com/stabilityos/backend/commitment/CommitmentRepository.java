package com.stabilityos.backend.commitment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CommitmentRepository extends JpaRepository<Commitment, Long> {

    List<Commitment> findAllByOrderByCreatedAtDescIdDesc();

    List<Commitment> findByStatusOrderByCreatedAtDescIdDesc(String status);

    List<Commitment> findByStatusAndDueDateLessThanEqualOrderByDueDateAscIdAsc(
            String status,
            LocalDate dueDate
    );
}