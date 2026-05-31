package com.stabilityos.backend.config;

import com.stabilityos.backend.memory.AssistantMemoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupDataInitializer implements CommandLineRunner {

    private final AssistantMemoryService assistantMemoryService;

    public StartupDataInitializer(AssistantMemoryService assistantMemoryService) {
        this.assistantMemoryService = assistantMemoryService;
    }

    @Override
    public void run(String... args) {
        assistantMemoryService.seedDefaultMemoriesIfMissing();
    }
}