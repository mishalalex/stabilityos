package com.stabilityos.backend.openloop;

import com.stabilityos.backend.openloop.dto.CreateOpenLoopRequest;
import com.stabilityos.backend.openloop.dto.OpenLoopDecisionRequest;
import com.stabilityos.backend.openloop.dto.OpenLoopResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/open-loops")
public class OpenLoopController {

    private final OpenLoopService openLoopService;

    @PostMapping
    public OpenLoopResponse createOpenLoop(
            @Valid @RequestBody CreateOpenLoopRequest request
    ) {
        return openLoopService.createOpenLoop(request);
    }

    @PostMapping("/from-input/{inputItemId}")
    public OpenLoopResponse createFromInput(@PathVariable Long inputItemId) {
        return openLoopService.createFromInput(inputItemId);
    }

    @PostMapping("/from-burden/{cognitiveBurdenId}")
    public OpenLoopResponse createFromBurden(@PathVariable Long cognitiveBurdenId) {
        return openLoopService.createFromBurden(cognitiveBurdenId);
    }

    @GetMapping
    public List<OpenLoopResponse> listOpenLoops(
            @RequestParam(required = false) String status
    ) {
        return openLoopService.listOpenLoops(status);
    }

    @GetMapping("/due")
    public List<OpenLoopResponse> listDueOpenLoops(
            @RequestParam(required = false) LocalDate date
    ) {
        return openLoopService.listDueOpenLoops(date);
    }

    @PostMapping("/{id}/close")
    public OpenLoopResponse closeOpenLoop(
            @PathVariable Long id,
            @RequestBody(required = false) OpenLoopDecisionRequest request
    ) {
        return openLoopService.closeOpenLoop(
                id,
                request == null ? null : request.note()
        );
    }

    @PostMapping("/{id}/park")
    public OpenLoopResponse parkOpenLoop(
            @PathVariable Long id,
            @RequestBody(required = false) OpenLoopDecisionRequest request
    ) {
        return openLoopService.parkOpenLoop(
                id,
                request == null ? null : request.note()
        );
    }
}