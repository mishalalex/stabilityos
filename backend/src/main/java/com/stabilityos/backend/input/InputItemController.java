package com.stabilityos.backend.input;

import com.stabilityos.backend.input.dto.CreateInputItemRequest;
import com.stabilityos.backend.input.dto.InputItemResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/input-items")
public class InputItemController {

    private final InputItemService inputItemService;

    public InputItemController(InputItemService inputItemService) {
        this.inputItemService = inputItemService;
    }

    @PostMapping
    public InputItemResponse createInputItem(@Valid @RequestBody CreateInputItemRequest request) {
        return inputItemService.createInputItem(request);
    }

    @GetMapping
    public List<InputItemResponse> listInputItems(
            @RequestParam(required = false) String status
    ) {
        return inputItemService.listInputItems(status);
    }
}