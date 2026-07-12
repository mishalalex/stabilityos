package com.stabilityos.backend.openloop;

import com.stabilityos.backend.burden.CognitiveBurden;
import com.stabilityos.backend.burden.CognitiveBurdenRepository;
import com.stabilityos.backend.input.InputItem;
import com.stabilityos.backend.input.InputItemRepository;
import com.stabilityos.backend.openloop.dto.CreateOpenLoopRequest;
import com.stabilityos.backend.openloop.dto.OpenLoopResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OpenLoopService {

    private static final String STATUS_OPEN = "open";

    private final OpenLoopRepository openLoopRepository;
    private final InputItemRepository inputItemRepository;
    private final CognitiveBurdenRepository cognitiveBurdenRepository;

    public OpenLoopResponse createOpenLoop(CreateOpenLoopRequest request) {
        OpenLoop openLoop = new OpenLoop(
                request.inputItemId(),
                request.cognitiveBurdenId(),
                normalizeTitle(request.title()),
                normalizeNullable(request.description()),
                normalizeType(request.loopType()),
                STATUS_OPEN,
                normalizeRequired(request.closureCondition()),
                normalizeNullable(request.nextAction()),
                request.nextReviewDate()
        );

        return toResponse(openLoopRepository.save(openLoop));
    }

    public OpenLoopResponse createFromInput(Long inputItemId) {
        InputItem inputItem = inputItemRepository.findById(inputItemId)
                .orElseThrow(() -> new IllegalArgumentException("Input item not found: " + inputItemId));

        String rawText = inputItem.getRawText();

        if (rawText == null || rawText.isBlank()) {
            rawText = "No text provided.";
        }

        OpenLoop openLoop = new OpenLoop(
                inputItem.getId(),
                null,
                buildTitleFromInput(inputItem),
                rawText,
                inferLoopType(rawText),
                STATUS_OPEN,
                "Decide whether this should be closed, parked, scheduled, or converted into a commitment.",
                "Review this input and choose the next handling path.",
                LocalDate.now()
        );

        return toResponse(openLoopRepository.save(openLoop));
    }

    public OpenLoopResponse createFromBurden(Long cognitiveBurdenId) {
        CognitiveBurden burden = cognitiveBurdenRepository.findById(cognitiveBurdenId)
                .orElseThrow(() -> new IllegalArgumentException("Cognitive burden not found: " + cognitiveBurdenId));

        OpenLoop openLoop = new OpenLoop(
                burden.getInputItemId(),
                burden.getId(),
                burden.getTitle(),
                burden.getDescription(),
                normalizeType(burden.getBurdenType()),
                STATUS_OPEN,
                buildClosureConditionFromBurden(burden),
                burden.getNextAction(),
                LocalDate.now()
        );

        return toResponse(openLoopRepository.save(openLoop));
    }

    public List<OpenLoopResponse> listOpenLoops(String status) {
        List<OpenLoop> openLoops;

        if (status == null || status.isBlank()) {
            openLoops = openLoopRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else {
            openLoops = openLoopRepository.findByStatusOrderByCreatedAtDescIdDesc(
                    normalizeType(status)
            );
        }

        return openLoops.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OpenLoopResponse> listDueOpenLoops(LocalDate date) {
        LocalDate reviewDate = date == null ? LocalDate.now() : date;

        return openLoopRepository
                .findByStatusAndNextReviewDateLessThanEqualOrderByNextReviewDateAscIdAsc(
                        STATUS_OPEN,
                        reviewDate
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OpenLoopResponse closeOpenLoop(Long id, String note) {
        OpenLoop openLoop = openLoopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Open loop not found: " + id));

        openLoop.close(normalizeNullable(note));
        return toResponse(openLoopRepository.save(openLoop));
    }

    public OpenLoopResponse parkOpenLoop(Long id, String note) {
        OpenLoop openLoop = openLoopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Open loop not found: " + id));

        openLoop.park(normalizeNullable(note));
        return toResponse(openLoopRepository.save(openLoop));
    }

    private String buildTitleFromInput(InputItem inputItem) {
        String domain = inputItem.getDetectedDomain();

        if (domain == null || domain.isBlank() || domain.equals("unknown")) {
            return "Resolve captured open loop";
        }

        return "Resolve " + domain.trim().toLowerCase() + " open loop";
    }

    private String inferLoopType(String rawText) {
        String text = rawText.toLowerCase();

        if (text.contains("decide") || text.contains("should i")) {
            return "decision";
        }

        if (text.contains("remind") || text.contains("don't forget") || text.contains("do not forget")) {
            return "reminder";
        }

        if (text.contains("need to") || text.contains("todo") || text.contains("task")) {
            return "task";
        }

        if (text.contains("worried") || text.contains("concerned") || text.contains("stress")) {
            return "worry";
        }

        return "open_loop";
    }

    private String buildClosureConditionFromBurden(CognitiveBurden burden) {
        if (burden.getNextAction() != null && !burden.getNextAction().isBlank()) {
            return "Complete or explicitly reject next action: " + burden.getNextAction();
        }

        return "Decide whether this burden should be closed, parked, scheduled, or converted into a commitment.";
    }

    private String normalizeTitle(String value) {
        return value.trim();
    }

    private String normalizeRequired(String value) {
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

    private OpenLoopResponse toResponse(OpenLoop openLoop) {
        return new OpenLoopResponse(
                openLoop.getId(),
                openLoop.getInputItemId(),
                openLoop.getCognitiveBurdenId(),
                openLoop.getTitle(),
                openLoop.getDescription(),
                openLoop.getLoopType(),
                openLoop.getStatus(),
                openLoop.getClosureCondition(),
                openLoop.getNextAction(),
                openLoop.getNextReviewDate(),
                openLoop.getCreatedAt(),
                openLoop.getUpdatedAt(),
                openLoop.getClosedAt(),
                openLoop.getClosureNote()
        );
    }
}