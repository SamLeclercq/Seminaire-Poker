package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
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
        TableState state = provider.loadInitialState();

        // Assert
        List<PlayerSeatState> seats = state.seats();
        long dealerCount = seats.stream().filter(PlayerSeatState::dealer).count();
        long actingCount = seats.stream().filter(PlayerSeatState::acting).count();

        assertAll(
                () -> assertEquals("LOCAL", state.tableCode()),
                () -> assertEquals("Flop", state.roundLabel()),
                () -> assertEquals(240, state.pot()),
                () -> assertEquals(5, state.communityCards().size()),
                () -> assertEquals(2, state.localPlayerCards().size()),
                () -> assertEquals(6, seats.size()),
                () -> assertEquals(1, dealerCount),
                () -> assertEquals(1, actingCount),
                () -> assertTrue(seats.stream().allMatch(PlayerSeatState::occupied)),
                () -> assertFalse(state.communityCards().isEmpty())
        );
    }
}
