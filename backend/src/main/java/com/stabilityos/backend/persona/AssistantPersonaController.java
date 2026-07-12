package com.stabilityos.backend.persona;

import com.stabilityos.backend.persona.dto.AssistantPersonaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AssistantPersonaController {

    private final AssistantPersonaService assistantPersonaService;

    @GetMapping("/api/assistant/persona")
    public AssistantPersonaResponse getPersona() {
        return assistantPersonaService.getPersona();
    }
}