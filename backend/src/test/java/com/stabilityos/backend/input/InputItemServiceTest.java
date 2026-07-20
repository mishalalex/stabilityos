package com.stabilityos.backend.input;

import com.stabilityos.backend.input.dto.CreateInputItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputItemServiceTest {
    @Mock
    private InputItemRepository inputItemRepository;

    @InjectMocks
    private InputItemService inputItemService;

    @Test
    void createInputItem_detectsFinanceDomain(){
        CreateInputItemRequest request = new CreateInputItemRequest(
                "telegram",
                "text",
                "Spend ₹250 on petrol",
                null,
                "123"
        );

        ArgumentCaptor<InputItem> captor = ArgumentCaptor.forClass(InputItem.class);

        when(inputItemRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        inputItemService.createInputItem(request);

        InputItem saved = captor.getValue();

        assertThat(saved.getSource()).isEqualTo("telegram");
        assertThat(saved.getInputType()).isEqualTo("text");
        assertThat(saved.getStatus()).isEqualTo("received");

        verify(inputItemRepository).save(saved);
    }

    @Test
    void createInputItem_detectsPlanningDomainForRemainderText() {
        CreateInputItemRequest request = new CreateInputItemRequest(
                "telegram",
                "text",
                "Remind me to review open loops tomorrow",
                null,
                "124"
        );

        ArgumentCaptor<InputItem> captor = ArgumentCaptor.forClass(InputItem.class);

        when(inputItemRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        inputItemService.createInputItem(request);

        assertThat(captor.getValue().getDetectedDomain()).isEqualTo("planning");
    }
}
