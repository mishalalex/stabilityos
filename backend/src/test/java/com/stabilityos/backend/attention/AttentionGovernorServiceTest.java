package com.stabilityos.backend.attention;

import com.stabilityos.backend.attention.dto.AttentionCheckResponse;
import com.stabilityos.backend.attention.dto.CreateAttentionCheckRequest;
import com.stabilityos.backend.commitment.CommitmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttentionGovernorServiceTest {

    @Mock
    private AttentionCheckRepository attentionCheckRepository;

    @Mock
    private CommitmentRepository commitmentRepository;

    @InjectMocks
    private AttentionGovernorService attentionGovernorService;

    @Test
    void createAttentionCheck_allowsUrgentAndImportantWork() {
        CreateAttentionCheckRequest request = new CreateAttentionCheckRequest(
                null,
                "manual",
                "work",
                "Fix production issue",
                "Possible customer impact",
                5,
                5
        );

        ArgumentCaptor<AttentionCheck> captor = ArgumentCaptor.forClass(AttentionCheck.class);

        when(attentionCheckRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttentionCheckResponse response = attentionGovernorService.createAttentionCheck(request);

        assertThat(response.decision()).isEqualTo("allowed_now");
        assertThat(response.decisionReason()).isEqualTo("This is both urgent and important.");
        assertThat(response.recommendedAction()).isEqualTo("Handle it now or schedule it immediately.");
    }

    @Test
    void createAttentionCheck_blocksLowValueScrolling() {
        CreateAttentionCheckRequest request = new CreateAttentionCheckRequest(
                null,
                "manual",
                "youtube",
                "Watch random video",
                "Low-energy context switch",
                1,
                1
        );

        when(attentionCheckRepository.save(any(AttentionCheck.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttentionCheckResponse response = attentionGovernorService.createAttentionCheck(request);

        assertThat(response.decision()).isEqualTo("blocked");
        assertThat(response.decisionReason())
                .isEqualTo("This looks like low-value attention capture rather than necessary action.");
    }

    @Test
    void createAttentionCheck_allowsCommitmentLinkedImportantAction() {
        CreateAttentionCheckRequest request = new CreateAttentionCheckRequest(
                10L,
                "manual",
                "admin",
                "Send document to accountant",
                "Linked to existing commitment",
                2,
                3
        );

        when(commitmentRepository.existsById(10L)).thenReturn(true);
        when(attentionCheckRepository.save(any(AttentionCheck.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttentionCheckResponse response = attentionGovernorService.createAttentionCheck(request);

        assertThat(response.decision()).isEqualTo("allowed_now");
        assertThat(response.decisionReason())
                .isEqualTo("This is tied to an existing commitment and has enough importance.");
    }

    @Test
    void listAttentionChecks_filtersByDecisionWhenProvided() {
        when(attentionCheckRepository.findByDecisionOrderByCreatedAtDescIdDesc("blocked"))
                .thenReturn(List.of());

        List<AttentionCheckResponse> response = attentionGovernorService.listAttentionChecks(" blocked ");

        assertThat(response).isEmpty();

        verify(attentionCheckRepository).findByDecisionOrderByCreatedAtDescIdDesc("blocked");
    }
}