package com.stabilityos.backend.assistant;

import com.stabilityos.backend.assistant.dto.AssistantRequest;
import com.stabilityos.backend.assistant.dto.AssistantResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/respond")
    public AssistantResponse respond(@Valid @RequestBody AssistantRequest request) {
        return assistantService.respond(request.message());
    }
}