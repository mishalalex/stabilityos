package com.stabilityos.backend.health;

import com.stabilityos.backend.health.dto.CreateHealthLogRequest;
import com.stabilityos.backend.health.dto.HealthLogResponse;
import com.stabilityos.backend.health.dto.HealthSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthLogController {

    private final HealthLogService healthLogService;

    public HealthLogController(HealthLogService healthLogService) {
        this.healthLogService = healthLogService;
    }

    @PostMapping("/logs")
    public HealthLogResponse createHealthLog(@Valid @RequestBody CreateHealthLogRequest request) {
        return healthLogService.createHealthLog(request);
    }

    @GetMapping("/logs")
    public List<HealthLogResponse> listHealthLogs() {
        return healthLogService.listHealthLogs();
    }

    @GetMapping("/summary")
    public HealthSummaryResponse healthSummary() {
        return healthLogService.getHealthSummary();
    }
}