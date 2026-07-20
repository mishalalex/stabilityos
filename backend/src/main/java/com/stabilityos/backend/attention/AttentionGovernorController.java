package com.stabilityos.backend.attention;

import com.stabilityos.backend.attention.dto.AttentionCheckResponse;
import com.stabilityos.backend.attention.dto.CreateAttentionCheckRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/attention")
public class AttentionGovernorController {

    private final AttentionGovernorService attentionGovernorService;

    @PostMapping("/checks")
    public AttentionCheckResponse createAttentionCheck(
            @Valid @RequestBody CreateAttentionCheckRequest request
    ) {
        return attentionGovernorService.createAttentionCheck(request);
    }

    @GetMapping("/checks")
    public List<AttentionCheckResponse> listAttentionChecks(
            @RequestParam(required = false) String decision
    ) {
        return attentionGovernorService.listAttentionChecks(decision);
    }
}