package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadTableStateServiceTest {

    @Test
    void shouldReturnStateFromProvider_whenLoadInitialState() {
        // Arrange
        TableUiState expectedState = new TableUiState(
                "AB123",
                "Turn",
                500,
                List.of("ace_of_spades", "king_of_spades"),
                List.of("queen_of_hearts", "queen_of_diamonds"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_000, true, true, false))
        );
        TableStateProvider provider = () -> expectedState;
        LoadTableStateService service = new LoadTableStateService(provider);

        // Act
        TableUiState actualState = service.loadInitialState();

        // Assert
        assertSame(expectedState, actualState);
    }

    @Test
    void shouldThrowException_whenTableStateProviderIsNull() {
        // Arrange

        // Act
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new LoadTableStateService(null)
        );

        // Assert
        assertEquals("tableStateProvider must not be null", exception.getMessage());
    }
}
