package com.stabilityos.backend.burden;

import com.stabilityos.backend.burden.dto.CognitiveBurdenResponse;
import com.stabilityos.backend.burden.dto.CreateCognitiveBurdenRequest;
import com.stabilityos.backend.input.InputItem;
import com.stabilityos.backend.input.InputItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CognitiveBurdenService {

    private static final String STATUS_OPEN = "open";

    private final CognitiveBurdenRepository cognitiveBurdenRepository;
    private final InputItemRepository inputItemRepository;

    public CognitiveBurdenResponse createBurden(CreateCognitiveBurdenRequest request) {
        CognitiveBurden burden = new CognitiveBurden(
                request.inputItemId(),
                normalizeTitle(request.title()),
                normalizeNullable(request.description()),
                normalizeType(request.burdenType()),
                STATUS_OPEN,
                request.burdenScore(),
                normalizeNullable(request.nextAction())
        );

        return toResponse(cognitiveBurdenRepository.save(burden));
    }

    public CognitiveBurdenResponse createBurdenFromInput(Long inputItemId) {
        InputItem inputItem = inputItemRepository.findById(inputItemId)
                .orElseThrow(() -> new IllegalArgumentException("Input item not found: " + inputItemId));

        String rawText = inputItem.getRawText();

        if (rawText == null || rawText.isBlank()) {
            rawText = "No text provided.";
        }

        CognitiveBurden burden = new CognitiveBurden(
                inputItem.getId(),
                buildTitle(inputItem),
                rawText,
                inferBurdenType(inputItem),
                STATUS_OPEN,
                3,
                "Review and decide what should happen next."
        );

        return toResponse(cognitiveBurdenRepository.save(burden));
    }

    public List<CognitiveBurdenResponse> listBurdens(String status) {
        List<CognitiveBurden> burdens;

        if (status == null || status.isBlank()) {
            burdens = cognitiveBurdenRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else {
            burdens = cognitiveBurdenRepository.findByStatusOrderByCreatedAtDescIdDesc(
                    status.trim().toLowerCase()
            );
        }

        return burdens.stream()
                .map(this::toResponse)
                .toList();
    }

    public CognitiveBurdenResponse closeBurden(Long id, String note) {
        CognitiveBurden burden = cognitiveBurdenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cognitive burden not found: " + id));

        burden.close(normalizeNullable(note));
        return toResponse(cognitiveBurdenRepository.save(burden));
    }

    public CognitiveBurdenResponse parkBurden(Long id, String note) {
        CognitiveBurden burden = cognitiveBurdenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cognitive burden not found: " + id));

        burden.park(normalizeNullable(note));
        return toResponse(cognitiveBurdenRepository.save(burden));
    }

    private String buildTitle(InputItem inputItem) {
        String domain = inputItem.getDetectedDomain();

        if (domain == null || domain.isBlank() || domain.equals("unknown")) {
            return "Review open loop";
        }

        return "Review " + domain.trim().toLowerCase() + " burden";
    }

    private String inferBurdenType(InputItem inputItem) {
        String rawText = inputItem.getRawText();

        if (rawText == null || rawText.isBlank()) {
            return "open_loop";
        }

        String text = rawText.toLowerCase();

        if (text.contains("decide") || text.contains("should i")) {
            return "decision";
        }

        if (text.contains("remind") || text.contains("don't forget") || text.contains("do not forget")) {
            return "reminder";
        }

        if (text.contains("worried") || text.contains("concerned") || text.contains("stress")) {
            return "worry";
        }

        if (text.contains("need to") || text.contains("todo") || text.contains("task")) {
            return "task";
        }

        return "open_loop";
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

    private CognitiveBurdenResponse toResponse(CognitiveBurden burden) {
        return new CognitiveBurdenResponse(
                burden.getId(),
                burden.getInputItemId(),
                burden.getTitle(),
                burden.getDescription(),
                burden.getBurdenType(),
                burden.getStatus(),
                burden.getBurdenScore(),
                burden.getNextAction(),
                burden.getCreatedAt(),
                burden.getUpdatedAt(),
                burden.getResolvedAt(),
                burden.getResolutionNote()
        );
    }
}