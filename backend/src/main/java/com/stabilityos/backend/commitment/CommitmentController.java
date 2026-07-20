package com.stabilityos.backend.commitment;

import com.stabilityos.backend.commitment.dto.CommitmentDecisionRequest;
import com.stabilityos.backend.commitment.dto.CommitmentResponse;
import com.stabilityos.backend.commitment.dto.CreateCommitmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/commitments")
public class CommitmentController {

    private final CommitmentService commitmentService;

    @PostMapping
    public CommitmentResponse createCommitment(
            @Valid @RequestBody CreateCommitmentRequest request
    ) {
        return commitmentService.createCommitment(request);
    }

    @PostMapping("/from-open-loop/{openLoopId}")
    public CommitmentResponse createFromOpenLoop(@PathVariable Long openLoopId) {
        return commitmentService.createFromOpenLoop(openLoopId);
    }

    @GetMapping
    public List<CommitmentResponse> listCommitments(
            @RequestParam(required = false) String status
    ) {
        return commitmentService.listCommitments(status);
    }

    @GetMapping("/due")
    public List<CommitmentResponse> listDueCommitments(
            @RequestParam(required = false) LocalDate date
    ) {
        return commitmentService.listDueCommitments(date);
    }

    @PostMapping("/{id}/complete")
    public CommitmentResponse completeCommitment(
            @PathVariable Long id,
            @RequestBody(required = false) CommitmentDecisionRequest request
    ) {
        return commitmentService.completeCommitment(
                id,
                request == null ? null : request.note()
        );
    }

    @PostMapping("/{id}/drop")
    public CommitmentResponse dropCommitment(
            @PathVariable Long id,
            @RequestBody(required = false) CommitmentDecisionRequest request
    ) {
        return commitmentService.dropCommitment(
                id,
                request == null ? null : request.note()
        );
    }
}