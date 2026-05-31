package com.stabilityos.backend.persona;

import com.stabilityos.backend.persona.dto.AssistantPersonaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssistantPersonaController {

    private final AssistantPersonaService assistantPersonaService;

    public AssistantPersonaController(AssistantPersonaService assistantPersonaService) {
        this.assistantPersonaService = assistantPersonaService;
    }

    @GetMapping("/api/assistant/persona")
    public AssistantPersonaResponse getPersona() {
        return assistantPersonaService.getPersona();
    }
}