package com.stabilityos.backend.commitment;

import com.stabilityos.backend.commitment.dto.CommitmentDecisionRequest;
import com.stabilityos.backend.commitment.dto.CommitmentResponse;
import com.stabilityos.backend.commitment.dto.CreateCommitmentRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommitmentControllerTest {

    private final CommitmentService commitmentService = mock(CommitmentService.class);
    private final CommitmentController commitmentController = new CommitmentController(commitmentService);

    @Test
    void createCommitment_delegatesToService() {
        CreateCommitmentRequest request = new CreateCommitmentRequest(
                null,
                "Pay bill",
                "Electricity bill",
                "admin",
                "high",
                LocalDate.of(2026, 7, 21)
        );

        CommitmentResponse expected = response("Pay bill", "open");

        when(commitmentService.createCommitment(request)).thenReturn(expected);

        CommitmentResponse actual = commitmentController.createCommitment(request);

        assertThat(actual).isEqualTo(expected);
        verify(commitmentService).createCommitment(request);
    }

    @Test
    void listCommitments_delegatesStatusFilterToService() {
        when(commitmentService.listCommitments("open"))
                .thenReturn(List.of(response("Pay bill", "open")));

        List<CommitmentResponse> actual = commitmentController.listCommitments("open");

        assertThat(actual).hasSize(1);
        verify(commitmentService).listCommitments("open");
    }

    @Test
    void completeCommitment_passesDecisionNoteToService() {
        CommitmentDecisionRequest request = new CommitmentDecisionRequest("Done");

        when(commitmentService.completeCommitment(1L, "Done"))
                .thenReturn(response("Pay bill", "completed"));

        CommitmentResponse actual = commitmentController.completeCommitment(1L, request);

        assertThat(actual.status()).isEqualTo("completed");
        verify(commitmentService).completeCommitment(1L, "Done");
    }

    @Test
    void dropCommitment_handlesMissingRequestBody() {
        when(commitmentService.dropCommitment(1L, null))
                .thenReturn(response("Pay bill", "dropped"));

        CommitmentResponse actual = commitmentController.dropCommitment(1L, null);

        assertThat(actual.status()).isEqualTo("dropped");
        verify(commitmentService).dropCommitment(1L, null);
    }

    private CommitmentResponse response(String title, String status) {
        return new CommitmentResponse(
                1L,
                null,
                title,
                null,
                "admin",
                status,
                "high",
                LocalDate.of(2026, 7, 21),
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}