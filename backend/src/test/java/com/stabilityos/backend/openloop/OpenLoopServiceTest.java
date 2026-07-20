package com.stabilityos.backend.openloop;

import com.stabilityos.backend.burden.CognitiveBurden;
import com.stabilityos.backend.burden.CognitiveBurdenRepository;
import com.stabilityos.backend.input.InputItem;
import com.stabilityos.backend.input.InputItemRepository;
import com.stabilityos.backend.openloop.dto.CreateOpenLoopRequest;
import com.stabilityos.backend.openloop.dto.OpenLoopResponse;
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
class OpenLoopServiceTest {

    @Mock
    private OpenLoopRepository openLoopRepository;

    @Mock
    private InputItemRepository inputItemRepository;

    @Mock
    private CognitiveBurdenRepository cognitiveBurdenRepository;

    @InjectMocks
    private OpenLoopService openLoopService;

    @Test
    void createOpenLoop_normalizesTypeAndTrimsTextFields() {
        CreateOpenLoopRequest request = new CreateOpenLoopRequest(
                null,
                null,
                "  Decide next phase  ",
                "  Avoid holding this in my head  ",
                "  Decision  ",
                "  Pick one next phase  ",
                "  Review roadmap  ",
                LocalDate.of(2026, 7, 12)
        );

        ArgumentCaptor<OpenLoop> captor = ArgumentCaptor.forClass(OpenLoop.class);

        when(openLoopRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OpenLoopResponse response = openLoopService.createOpenLoop(request);

        assertThat(response.title()).isEqualTo("Decide next phase");
        assertThat(response.description()).isEqualTo("Avoid holding this in my head");
        assertThat(response.loopType()).isEqualTo("decision");
        assertThat(response.status()).isEqualTo("open");
        assertThat(response.closureCondition()).isEqualTo("Pick one next phase");
        assertThat(response.nextAction()).isEqualTo("Review roadmap");
        assertThat(response.nextReviewDate()).isEqualTo(LocalDate.of(2026, 7, 12));

        verify(openLoopRepository).save(captor.getValue());
    }

    @Test
    void createFromInput_infersReminderLoopType() {
        InputItem inputItem = new InputItem(
                "telegram",
                "text",
                "Remind me to pay car loan",
                null,
                "123",
                "received",
                "planning",
                null
        );

        ArgumentCaptor<OpenLoop> captor = ArgumentCaptor.forClass(OpenLoop.class);

        when(inputItemRepository.findById(1L)).thenReturn(Optional.of(inputItem));
        when(openLoopRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OpenLoopResponse response = openLoopService.createFromInput(1L);

        assertThat(response.title()).isEqualTo("Resolve planning open loop");
        assertThat(response.description()).isEqualTo("Remind me to pay car loan");
        assertThat(response.loopType()).isEqualTo("reminder");
        assertThat(response.status()).isEqualTo("open");
        assertThat(response.closureCondition())
                .isEqualTo("Decide whether this should be closed, parked, scheduled, or converted into a commitment.");

        verify(openLoopRepository).save(captor.getValue());
    }

    @Test
    void createFromBurden_usesBurdenNextActionAsClosureCondition() {
        CognitiveBurden burden = new CognitiveBurden(
                7L,
                "Review finance burden",
                "Need to decide whether to close car loan early",
                "decision",
                "open",
                4,
                "Compare loan closure versus keeping emergency fund"
        );

        ArgumentCaptor<OpenLoop> captor = ArgumentCaptor.forClass(OpenLoop.class);

        when(cognitiveBurdenRepository.findById(10L)).thenReturn(Optional.of(burden));
        when(openLoopRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OpenLoopResponse response = openLoopService.createFromBurden(10L);

        assertThat(response.title()).isEqualTo("Review finance burden");
        assertThat(response.loopType()).isEqualTo("decision");
        assertThat(response.status()).isEqualTo("open");
        assertThat(response.closureCondition())
                .isEqualTo("Complete or explicitly reject next action: Compare loan closure versus keeping emergency fund");
    }

    @Test
    void listDueOpenLoops_usesTodayWhenDateIsMissing() {
        when(openLoopRepository.findByStatusAndNextReviewDateLessThanEqualOrderByNextReviewDateAscIdAsc(
                "open",
                LocalDate.now()
        )).thenReturn(List.of());

        List<OpenLoopResponse> response = openLoopService.listDueOpenLoops(null);

        assertThat(response).isEmpty();

        verify(openLoopRepository)
                .findByStatusAndNextReviewDateLessThanEqualOrderByNextReviewDateAscIdAsc(
                        "open",
                        LocalDate.now()
                );
    }
}