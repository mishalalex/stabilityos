package com.stabilityos.backend.attention;

import com.stabilityos.backend.attention.dto.AttentionCheckResponse;
import com.stabilityos.backend.attention.dto.CreateAttentionCheckRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AttentionGovernorControllerTest {

    private final AttentionGovernorService attentionGovernorService = mock(AttentionGovernorService.class);
    private final AttentionGovernorController attentionGovernorController =
            new AttentionGovernorController(attentionGovernorService);

    @Test
    void createAttentionCheck_delegatesToService() {
        CreateAttentionCheckRequest request = new CreateAttentionCheckRequest(
                null,
                "manual",
                "work",
                "Fix issue",
                "Important task",
                4,
                4
        );

        AttentionCheckResponse expected = response("Fix issue", "allowed_now");

        when(attentionGovernorService.createAttentionCheck(request)).thenReturn(expected);

        AttentionCheckResponse actual = attentionGovernorController.createAttentionCheck(request);

        assertThat(actual).isEqualTo(expected);
        verify(attentionGovernorService).createAttentionCheck(request);
    }

    @Test
    void listAttentionChecks_delegatesDecisionFilterToService() {
        when(attentionGovernorService.listAttentionChecks("blocked"))
                .thenReturn(List.of(response("Watch random video", "blocked")));

        List<AttentionCheckResponse> actual = attentionGovernorController.listAttentionChecks("blocked");

        assertThat(actual).hasSize(1);
        verify(attentionGovernorService).listAttentionChecks("blocked");
    }

    private AttentionCheckResponse response(String title, String decision) {
        return new AttentionCheckResponse(
                1L,
                null,
                "manual",
                "work",
                title,
                null,
                4,
                4,
                decision,
                "reason",
                "action",
                LocalDateTime.now()
        );
    }
}