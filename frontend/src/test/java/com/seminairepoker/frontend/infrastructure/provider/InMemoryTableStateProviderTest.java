package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTableStateProviderTest {

    @Test
    void shouldProvideConsistentInitialTableState_whenLoadInitialState() {
        // Arrange
        InMemoryTableStateProvider provider = new InMemoryTableStateProvider();

        // Act
        TableUiState state = provider.loadInitialState();

        // Assert
        List<PlayerSeatUiState> seats = state.seats();
        long dealerCount = seats.stream().filter(PlayerSeatUiState::dealer).count();
        long actingCount = seats.stream().filter(PlayerSeatUiState::acting).count();

        assertAll(
                () -> assertEquals("Flop", state.roundLabel()),
                () -> assertEquals(240, state.pot()),
                () -> assertEquals(5, state.communityCards().size()),
                () -> assertEquals(2, state.localPlayerCards().size()),
                () -> assertEquals(6, seats.size()),
                () -> assertEquals(1, dealerCount),
                () -> assertEquals(1, actingCount),
                () -> assertTrue(seats.stream().allMatch(PlayerSeatUiState::occupied)),
                () -> assertFalse(state.communityCards().isEmpty())
        );
    }
}

