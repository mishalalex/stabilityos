package com.stabilityos.backend.commitment;

import com.stabilityos.backend.commitment.dto.CommitmentResponse;
import com.stabilityos.backend.commitment.dto.CreateCommitmentRequest;
import com.stabilityos.backend.openloop.OpenLoop;
import com.stabilityos.backend.openloop.OpenLoopRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommitmentServiceTest {

    @Mock
    private CommitmentRepository commitmentRepository;

    @Mock
    private OpenLoopRepository openLoopRepository;

    @InjectMocks
    private CommitmentService commitmentService;

    @Test
    void createCommitment_normalizesFieldsAndCreatesOpenCommitment() {
        CreateCommitmentRequest request = new CreateCommitmentRequest(
                null,
                "  Pay car loan EMI  ",
                "  Must be handled before due date  ",
                "  Finance  ",
                "  High  ",
                LocalDate.of(2026, 7, 25)
        );

        ArgumentCaptor<Commitment> captor = ArgumentCaptor.forClass(Commitment.class);

        when(commitmentRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommitmentResponse response = commitmentService.createCommitment(request);

        assertThat(response.title()).isEqualTo("Pay car loan EMI");
        assertThat(response.description()).isEqualTo("Must be handled before due date");
        assertThat(response.commitmentType()).isEqualTo("finance");
        assertThat(response.priority()).isEqualTo("high");
        assertThat(response.status()).isEqualTo("open");
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 7, 25));

        verify(commitmentRepository).save(captor.getValue());
    }

    @Test
    void createFromOpenLoop_buildsCommitmentFromOpenLoop() {
        OpenLoop openLoop = new OpenLoop(
                1L,
                2L,
                "Resolve finance open loop",
                "Need to decide whether to pay loan early",
                "decision",
                "open",
                "Pick one option",
                "Compare loan closure versus emergency fund",
                LocalDate.of(2026, 7, 22)
        );

        ArgumentCaptor<Commitment> captor = ArgumentCaptor.forClass(Commitment.class);

        when(openLoopRepository.findById(10L)).thenReturn(Optional.of(openLoop));
        when(commitmentRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommitmentResponse response = commitmentService.createFromOpenLoop(10L);

        assertThat(response.title()).isEqualTo("Resolve finance open loop");
        assertThat(response.description()).isEqualTo("Need to decide whether to pay loan early");
        assertThat(response.commitmentType()).isEqualTo("decision");
        assertThat(response.priority()).isEqualTo("medium");
        assertThat(response.status()).isEqualTo("open");
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 7, 22));
    }

    @Test
    void listDueCommitments_usesTodayWhenDateIsMissing() {
        when(commitmentRepository.findByStatusAndDueDateLessThanEqualOrderByDueDateAscIdAsc(
                "open",
                LocalDate.now()
        )).thenReturn(List.of());

        List<CommitmentResponse> response = commitmentService.listDueCommitments(null);

        assertThat(response).isEmpty();

        verify(commitmentRepository)
                .findByStatusAndDueDateLessThanEqualOrderByDueDateAscIdAsc(
                        "open",
                        LocalDate.now()
                );
    }

    @Test
    void completeCommitment_marksCommitmentCompleted() {
        Commitment commitment = new Commitment(
                null,
                "Send document",
                "Send document to accountant",
                "admin",
                "open",
                "high",
                LocalDate.of(2026, 7, 21)
        );

        when(commitmentRepository.findById(1L)).thenReturn(Optional.of(commitment));
        when(commitmentRepository.save(commitment)).thenReturn(commitment);

        CommitmentResponse response = commitmentService.completeCommitment(
                1L,
                "Sent successfully"
        );

        assertThat(response.status()).isEqualTo("completed");
        assertThat(response.completedAt()).isNotNull();
        assertThat(response.outcomeNote()).isEqualTo("Sent successfully");
    }

    @Test
    void dropCommitment_marksCommitmentDropped() {
        Commitment commitment = new Commitment(
                null,
                "Review unused idea",
                "Not worth doing this week",
                "planning",
                "open",
                "low",
                null
        );

        when(commitmentRepository.findById(1L)).thenReturn(Optional.of(commitment));
        when(commitmentRepository.save(commitment)).thenReturn(commitment);

        CommitmentResponse response = commitmentService.dropCommitment(
                1L,
                "No longer relevant"
        );

        assertThat(response.status()).isEqualTo("dropped");
        assertThat(response.droppedAt()).isNotNull();
        assertThat(response.outcomeNote()).isEqualTo("No longer relevant");
    }
}