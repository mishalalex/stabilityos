package com.stabilityos.backend.draft;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionDraftRepository extends JpaRepository<ActionDraft, Long> {

    List<ActionDraft> findAllByOrderByCreatedAtDescIdDesc();

    List<ActionDraft> findByStatusOrderByCreatedAtDescIdDesc(String status);
}