package com.stabilityos.backend.burden;

import com.stabilityos.backend.burden.dto.BurdenDecisionRequest;
import com.stabilityos.backend.burden.dto.CognitiveBurdenResponse;
import com.stabilityos.backend.burden.dto.CreateCognitiveBurdenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cognitive-burdens")
public class CognitiveBurdenController {

    private final CognitiveBurdenService cognitiveBurdenService;

    @PostMapping
    public CognitiveBurdenResponse createBurden(
            @Valid @RequestBody CreateCognitiveBurdenRequest request
    ) {
        return cognitiveBurdenService.createBurden(request);
    }

    @PostMapping("/from-input/{inputItemId}")
    public CognitiveBurdenResponse createBurdenFromInput(@PathVariable Long inputItemId) {
        return cognitiveBurdenService.createBurdenFromInput(inputItemId);
    }

    @GetMapping
    public List<CognitiveBurdenResponse> listBurdens(
            @RequestParam(required = false) String status
    ) {
        return cognitiveBurdenService.listBurdens(status);
    }

    @PostMapping("/{id}/close")
    public CognitiveBurdenResponse closeBurden(
            @PathVariable Long id,
            @RequestBody(required = false) BurdenDecisionRequest request
    ) {
        return cognitiveBurdenService.closeBurden(
                id,
                request == null ? null : request.note()
        );
    }

    @PostMapping("/{id}/park")
    public CognitiveBurdenResponse parkBurden(
            @PathVariable Long id,
            @RequestBody(required = false) BurdenDecisionRequest request
    ) {
        return cognitiveBurdenService.parkBurden(
                id,
                request == null ? null : request.note()
        );
    }
}