package com.stabilityos.backend.commitment;

import com.stabilityos.backend.commitment.dto.CommitmentResponse;
import com.stabilityos.backend.commitment.dto.CreateCommitmentRequest;
import com.stabilityos.backend.openloop.OpenLoop;
import com.stabilityos.backend.openloop.OpenLoopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommitmentService {

    private static final String STATUS_OPEN = "open";

    private final CommitmentRepository commitmentRepository;
    private final OpenLoopRepository openLoopRepository;

    public CommitmentResponse createCommitment(CreateCommitmentRequest request) {
        Commitment commitment = new Commitment(
                request.openLoopId(),
                normalizeTitle(request.title()),
                normalizeNullable(request.description()),
                normalizeType(request.commitmentType()),
                STATUS_OPEN,
                normalizeType(request.priority()),
                request.dueDate()
        );

        return toResponse(commitmentRepository.save(commitment));
    }

    public CommitmentResponse createFromOpenLoop(Long openLoopId) {
        OpenLoop openLoop = openLoopRepository.findById(openLoopId)
                .orElseThrow(() -> new IllegalArgumentException("Open loop not found: " + openLoopId));

        Commitment commitment = new Commitment(
                openLoop.getId(),
                openLoop.getTitle(),
                openLoop.getDescription(),
                normalizeType(openLoop.getLoopType()),
                STATUS_OPEN,
                "medium",
                openLoop.getNextReviewDate()
        );

        return toResponse(commitmentRepository.save(commitment));
    }

    public List<CommitmentResponse> listCommitments(String status) {
        List<Commitment> commitments;

        if (status == null || status.isBlank()) {
            commitments = commitmentRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else {
            commitments = commitmentRepository.findByStatusOrderByCreatedAtDescIdDesc(
                    normalizeType(status)
            );
        }

        return commitments.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CommitmentResponse> listDueCommitments(LocalDate date) {
        LocalDate dueDate = date == null ? LocalDate.now() : date;

        return commitmentRepository
                .findByStatusAndDueDateLessThanEqualOrderByDueDateAscIdAsc(
                        STATUS_OPEN,
                        dueDate
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CommitmentResponse completeCommitment(Long id, String note) {
        Commitment commitment = commitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commitment not found: " + id));

        commitment.complete(normalizeNullable(note));
        return toResponse(commitmentRepository.save(commitment));
    }

    public CommitmentResponse dropCommitment(Long id, String note) {
        Commitment commitment = commitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commitment not found: " + id));

        commitment.drop(normalizeNullable(note));
        return toResponse(commitmentRepository.save(commitment));
    }

    private String normalizeTitle(String value) {
        return value.trim();
    }

    private String normalizeType(String value) {
        return value.trim().toLowerCase();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private CommitmentResponse toResponse(Commitment commitment) {
        return new CommitmentResponse(
                commitment.getId(),
                commitment.getOpenLoopId(),
                commitment.getTitle(),
                commitment.getDescription(),
                commitment.getCommitmentType(),
                commitment.getStatus(),
                commitment.getPriority(),
                commitment.getDueDate(),
                commitment.getCompletedAt(),
                commitment.getDroppedAt(),
                commitment.getCreatedAt(),
                commitment.getUpdatedAt(),
                commitment.getOutcomeNote()
        );
    }
}