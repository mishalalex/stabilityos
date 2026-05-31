package com.stabilityos.backend.memory;

import com.stabilityos.backend.memory.dto.MemoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantMemoryService {

    private final AssistantMemoryRepository repository;

    public AssistantMemoryService(AssistantMemoryRepository repository) {
        this.repository = repository;
    }

    public void seedDefaultMemoriesIfMissing() {
        createIfMissing(
                "tone_preference",
                "User prefers direct, practical, no-fluff guidance.",
                "preference",
                5
        );

        createIfMissing(
                "ai_usage_rule",
                "AI should scaffold thinking, not replace understanding. User should be able to explain important AI-generated work.",
                "principle",
                5
        );

        createIfMissing(
                "career_focus",
                "Spring Boot, backend testing, SDET growth, and StabilityOS are important career-compounding areas.",
                "career",
                4
        );

        createIfMissing(
                "behavior_risk",
                "User may drift into YouTube, scrolling, or over-researching tools instead of executing.",
                "behavior",
                4
        );

        createIfMissing(
                "health_focus",
                "Health consistency matters: sleep, water, weight, and routine tracking should not be ignored.",
                "health",
                4
        );
    }

    public List<MemoryResponse> getTopMemories() {
        return repository.findTop10ByOrderByImportanceDescUpdatedAtDesc()
                .stream()
                .map(memory -> new MemoryResponse(
                        memory.getKey(),
                        memory.getValue(),
                        memory.getType(),
                        memory.getImportance()
                ))
                .toList();
    }

    private void createIfMissing(String key, String value, String type, int importance) {
        repository.findByKey(key)
                .orElseGet(() -> repository.save(new AssistantMemory(key, value, type, importance)));
    }
}