package com.stabilityos.backend.assistant;

import com.stabilityos.backend.assistant.dto.AssistantRequest;
import com.stabilityos.backend.assistant.dto.AssistantResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/respond")
    public AssistantResponse respond(@Valid @RequestBody AssistantRequest request) {
        return assistantService.respond(request.message());
    }
}