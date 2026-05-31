package com.stabilityos.backend.draft;

import com.stabilityos.backend.draft.dto.ActionDraftResponse;
import com.stabilityos.backend.draft.dto.DraftDecisionRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/action-drafts")
public class ActionDraftController {

    private final ActionDraftService actionDraftService;

    public ActionDraftController(ActionDraftService actionDraftService) {
        this.actionDraftService = actionDraftService;
    }

    @PostMapping("/from-input/{inputItemId}")
    public ActionDraftResponse createDraftFromInput(@PathVariable Long inputItemId) {
        return actionDraftService.createDraftFromInput(inputItemId);
    }

    @GetMapping
    public List<ActionDraftResponse> listDrafts(
            @RequestParam(required = false) String status
    ) {
        return actionDraftService.listDrafts(status);
    }

    @PostMapping("/{id}/confirm")
    public ActionDraftResponse confirmDraft(
            @PathVariable Long id,
            @RequestBody(required = false) DraftDecisionRequest request
    ) {
        return actionDraftService.confirmDraft(
                id,
                request == null ? null : request.note()
        );
    }

    @PostMapping("/{id}/reject")
    public ActionDraftResponse rejectDraft(
            @PathVariable Long id,
            @RequestBody(required = false) DraftDecisionRequest request
    ) {
        return actionDraftService.rejectDraft(
                id,
                request == null ? null : request.note()
        );
    }
}