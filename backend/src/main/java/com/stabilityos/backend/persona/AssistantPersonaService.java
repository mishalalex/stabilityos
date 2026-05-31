package com.stabilityos.backend.persona;

import com.stabilityos.backend.memory.AssistantMemoryService;
import com.stabilityos.backend.memory.dto.MemoryResponse;
import com.stabilityos.backend.persona.dto.AssistantPersonaResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantPersonaService {

    private final AssistantMemoryService assistantMemoryService;

    public AssistantPersonaService(AssistantMemoryService assistantMemoryService) {
        this.assistantMemoryService = assistantMemoryService;
    }

    public AssistantPersonaResponse getPersona() {
        List<String> principles = assistantMemoryService.getTopMemories()
                .stream()
                .map(MemoryResponse::value)
                .toList();

        return new AssistantPersonaResponse(
                "direct-practical",
                "Direct, practical, no-fluff, execution-focused",
                "Give clear next actions. Reduce overthinking. Preserve user understanding. Avoid unnecessary reassurance.",
                principles
        );
    }

    public String personaLine() {
        return "Direct mode: be practical, reduce overthinking, preserve understanding, and focus on the next useful action.";
    }

    public String unknownIntentGuidance() {
        return """
                I can help with:
                - What should I do today?
                - How did I do today?
                - How did my week go?

                I will keep guidance direct and practical. The goal is consistency, not overthinking.
                """;
    }
}