package com.stabilityos.backend.input;

import com.stabilityos.backend.input.dto.CreateInputItemRequest;
import com.stabilityos.backend.input.dto.InputItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InputItemService {

    private static final String STATUS_RECEIVED = "received";

    private final InputItemRepository inputItemRepository;

    public InputItemResponse createInputItem(CreateInputItemRequest request) {
        InputItem inputItem = new InputItem(
                normalize(request.source()),
                normalize(request.inputType()),
                normalizeNullable(request.rawText()),
                normalizeNullable(request.filePath()),
                normalizeNullable(request.telegramMessageId()),
                STATUS_RECEIVED,
                detectDomain(request.rawText()),
                null
        );

        InputItem saved = inputItemRepository.save(inputItem);
        return toResponse(saved);
    }

    public List<InputItemResponse> listInputItems(String status) {
        List<InputItem> items;

        if (status == null || status.isBlank()) {
            items = inputItemRepository.findAllByOrderByCreatedAtDescIdDesc();
        } else {
            items = inputItemRepository.findByStatusOrderByCreatedAtDescIdDesc(
                    normalize(status)
            );
        }

        return items.stream()
                .map(this::toResponse)
                .toList();
    }

    private String detectDomain(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "unknown";
        }

        String text = rawText.toLowerCase();

        if (
                text.contains("spent")
                        || text.contains("paid")
                        || text.contains("expense")
                        || text.contains("rs")
                        || text.contains("₹")
                        || text.matches(".*\\b\\d+\\s*(tea|coffee|food|petrol|diesel|milk|snack).*")
        ) {
            return "finance";
        }

        if (
                text.contains("sleep")
                        || text.contains("slept")
                        || text.contains("water")
                        || text.contains("weight")
                        || text.contains("mood")
        ) {
            return "health";
        }

        if (
                text.contains("breakfast")
                        || text.contains("lunch")
                        || text.contains("dinner")
                        || text.contains("snack")
                        || text.contains("ate")
                        || text.contains("had")
                        || text.contains("food")
                        || text.contains("meal")
        ) {
            return "food";
        }

        if (
                text.contains("news")
                        || text.contains("headline")
                        || text.contains("report")
                        || text.contains("article")
        ) {
            return "news";
        }

        if (
                text.contains("task")
                        || text.contains("plan")
                        || text.contains("today")
                        || text.contains("tomorrow")
                        || text.contains("remind")
        ) {
            return "planning";
        }

        return "unknown";
    }

    private InputItemResponse toResponse(InputItem inputItem) {
        return new InputItemResponse(
                inputItem.getId(),
                inputItem.getSource(),
                inputItem.getInputType(),
                inputItem.getRawText(),
                inputItem.getFilePath(),
                inputItem.getTelegramMessageId(),
                inputItem.getStatus(),
                inputItem.getDetectedDomain(),
                inputItem.getErrorMessage(),
                inputItem.getCreatedAt(),
                inputItem.getProcessedAt()
        );
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}