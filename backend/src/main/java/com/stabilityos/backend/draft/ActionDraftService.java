package com.stabilityos.backend.draft;

import com.stabilityos.backend.draft.dto.ActionDraftResponse;
import com.stabilityos.backend.input.InputItem;
import com.stabilityos.backend.input.InputItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ActionDraftService {

    private static final String STATUS_PENDING = "pending";

    private final ActionDraftRepository actionDraftRepository;
    private final InputItemRepository inputItemRepository;

    public ActionDraftResponse createDraftFromInput(Long inputItemId) {
        InputItem inputItem = inputItemRepository.findById(inputItemId)
                .orElseThrow(() -> new IllegalArgumentException("Input item not found: " + inputItemId));

        ActionDraft draft = new ActionDraft(
                inputItem.getId(),
                normalizeDraftType(inputItem.getDetectedDomain()),
                buildTitle(inputItem),
                buildProposedAction(inputItem),
                STATUS_PENDING
        );

        return toResponse(actionDraftRepository.save(draft));
    }

    public List<ActionDraftResponse> listDrafts(String status) {
        List<ActionDraft> drafts;

        if (status == null || status.isBlank()) {
            drafts = actionDraftRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else {
            drafts = actionDraftRepository.findByStatusOrderByCreatedAtDescIdDesc(status.trim().toLowerCase());
        }

        return drafts.stream()
                .map(this::toResponse)
                .toList();
    }

    public ActionDraftResponse confirmDraft(Long id, String note) {
        ActionDraft draft = actionDraftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found: " + id));

        draft.confirm(normalizeNullable(note));
        return toResponse(actionDraftRepository.save(draft));
    }

    public ActionDraftResponse rejectDraft(Long id, String note) {
        ActionDraft draft = actionDraftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found: " + id));

        draft.reject(normalizeNullable(note));
        return toResponse(actionDraftRepository.save(draft));
    }

    private String normalizeDraftType(String detectedDomain) {
        if (detectedDomain == null || detectedDomain.isBlank()) {
            return "unknown";
        }

        return detectedDomain.trim().toLowerCase();
    }

    private String buildTitle(InputItem inputItem) {
        return switch (normalizeDraftType(inputItem.getDetectedDomain())) {
            case "finance" -> "Review finance entry";
            case "health" -> "Review health entry";
            case "food" -> "Review food entry";
            case "planning" -> "Review planning entry";
            case "news" -> "Review news entry";
            default -> "Review captured input";
        };
    }

    private String buildProposedAction(InputItem inputItem) {
        String rawText = inputItem.getRawText();

        if (rawText == null || rawText.isBlank()) {
            rawText = "No text provided.";
        }

        return """
                Proposed action:
                Review this captured input before taking any system action.

                Detected domain: %s

                Original input:
                %s
                """.formatted(
                normalizeDraftType(inputItem.getDetectedDomain()),
                rawText
        );
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private ActionDraftResponse toResponse(ActionDraft draft) {
        return new ActionDraftResponse(
                draft.getId(),
                draft.getInputItemId(),
                draft.getDraftType(),
                draft.getTitle(),
                draft.getProposedAction(),
                draft.getStatus(),
                draft.getCreatedAt(),
                draft.getDecidedAt(),
                draft.getDecisionNote()
        );
    }
}