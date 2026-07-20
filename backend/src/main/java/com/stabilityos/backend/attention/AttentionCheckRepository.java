package com.stabilityos.backend.attention;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttentionCheckRepository extends JpaRepository<AttentionCheck, Long> {

    List<AttentionCheck> findAllByOrderByCreatedAtDescIdDesc();

    List<AttentionCheck> findByDecisionOrderByCreatedAtDescIdDesc(String decision);
}