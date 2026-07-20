package com.stabilityos.backend.attention;

import com.stabilityos.backend.attention.dto.AttentionCheckResponse;
import com.stabilityos.backend.attention.dto.CreateAttentionCheckRequest;
import com.stabilityos.backend.commitment.CommitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AttentionGovernorService {

    private static final String DECISION_ALLOWED_NOW = "allowed_now";
    private static final String DECISION_DEFERRED = "deferred";
    private static final String DECISION_BLOCKED = "blocked";

    private static final Set<String> LOW_VALUE_ACTIVITY_TYPES = Set.of(
            "scrolling",
            "youtube",
            "social",
            "news",
            "shopping",
            "research"
    );

    private final AttentionCheckRepository attentionCheckRepository;
    private final CommitmentRepository commitmentRepository;

    public AttentionCheckResponse createAttentionCheck(CreateAttentionCheckRequest request) {
        if (request.commitmentId() != null && !commitmentRepository.existsById(request.commitmentId())) {
            throw new IllegalArgumentException("Commitment not found: " + request.commitmentId());
        }

        String source = normalizeType(request.source());
        String activityType = normalizeType(request.activityType());
        String title = normalizeTitle(request.title());
        String description = normalizeNullable(request.description());

        AttentionDecision decision = decide(
                request.commitmentId(),
                activityType,
                request.urgencyScore(),
                request.importanceScore()
        );

        AttentionCheck attentionCheck = new AttentionCheck(
                request.commitmentId(),
                source,
                activityType,
                title,
                description,
                request.urgencyScore(),
                request.importanceScore(),
                decision.decision(),
                decision.reason(),
                decision.recommendedAction()
        );

        return toResponse(attentionCheckRepository.save(attentionCheck));
    }

    public List<AttentionCheckResponse> listAttentionChecks(String decision) {
        List<AttentionCheck> checks;

        if (decision == null || decision.isBlank()) {
            checks = attentionCheckRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else {
            checks = attentionCheckRepository.findByDecisionOrderByCreatedAtDescIdDesc(
                    normalizeType(decision)
            );
        }

        return checks.stream()
                .map(this::toResponse)
                .toList();
    }

    private AttentionDecision decide(
            Long commitmentId,
            String activityType,
            int urgencyScore,
            int importanceScore
    ) {
        if (commitmentId != null && importanceScore >= 3) {
            return new AttentionDecision(
                    DECISION_ALLOWED_NOW,
                    "This is tied to an existing commitment and has enough importance.",
                    "Proceed, but keep the action bounded and update the commitment afterwards."
            );
        }

        if (urgencyScore >= 4 && importanceScore >= 4) {
            return new AttentionDecision(
                    DECISION_ALLOWED_NOW,
                    "This is both urgent and important.",
                    "Handle it now or schedule it immediately."
            );
        }

        if (LOW_VALUE_ACTIVITY_TYPES.contains(activityType) && urgencyScore <= 3 && importanceScore <= 3) {
            return new AttentionDecision(
                    DECISION_BLOCKED,
                    "This looks like low-value attention capture rather than necessary action.",
                    "Do not switch context. Park it only if it still matters after the current work block."
            );
        }

        if (urgencyScore <= 2 && importanceScore <= 3) {
            return new AttentionDecision(
                    DECISION_DEFERRED,
                    "This is not urgent enough to interrupt current attention.",
                    "Defer it and review later through open loops or commitments."
            );
        }

        return new AttentionDecision(
                DECISION_DEFERRED,
                "This may matter, but it does not justify immediate context switching.",
                "Capture it, then return to the current priority."
        );
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

    private AttentionCheckResponse toResponse(AttentionCheck check) {
        return new AttentionCheckResponse(
                check.getId(),
                check.getCommitmentId(),
                check.getSource(),
                check.getActivityType(),
                check.getTitle(),
                check.getDescription(),
                check.getUrgencyScore(),
                check.getImportanceScore(),
                check.getDecision(),
                check.getDecisionReason(),
                check.getRecommendedAction(),
                check.getCreatedAt()
        );
    }

    private record AttentionDecision(
            String decision,
            String reason,
            String recommendedAction
    ) {
    }
}