package com.stabilityos.backend.memory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssistantMemoryRepository extends JpaRepository<AssistantMemory, Long> {

    Optional<AssistantMemory> findByKey(String key);

    List<AssistantMemory> findTop10ByOrderByImportanceDescUpdatedAtDesc();
}