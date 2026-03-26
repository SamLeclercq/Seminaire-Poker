package com.seminairepoker.frontend.infrastructure.websocket.adapter;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendPlayerTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BackendTableStateAdapterTest {

    @Test
    void shouldPreferCurrentPlayerPocket_whenTopLevelPocketContainsOnlyOneCard() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of(),
                List.of("ace_of_spades"),
                List.of(
                        new BackendPlayerTransport(
                                "Nina",
                                980,
                                false,
                                true,
                                true,
                                true,
                                true,
                                true,
                                List.of("ace_of_spades", "ace_of_hearts")
                        ),
                        new BackendPlayerTransport(
                                "Leo",
                                970,
                                true,
                                false,
                                false,
                                true,
                                true,
                                true,
                                List.of()
                        )
                )
        );
        BackendTableStateAdapter adapter = new BackendTableStateAdapter();

        // Act
        TableState state = adapter.toTableState(payload);

        // Assert
        assertEquals(List.of("ace_of_spades", "ace_of_hearts"), state.localPlayerCards());
    }

    @Test
    void shouldUseCurrentPlayerPocket_whenTopLevelPlayerPocketIsMissing() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of(),
                null,
                List.of(
                        new BackendPlayerTransport(
                                "Nina",
                                980,
                                false,
                                true,
                                true,
                                true,
                                true,
                                true,
                                List.of("king_of_spades", "king_of_hearts")
                        ),
                        new BackendPlayerTransport(
                                "Leo",
                                970,
                                true,
                                false,
                                false,
                                true,
                                true,
                                true,
                                List.of()
                        )
                )
        );
        BackendTableStateAdapter adapter = new BackendTableStateAdapter();

        // Act
        TableState state = adapter.toTableState(payload);

        // Assert
        assertEquals("AB123", state.tableCode());
        assertEquals("preflop", state.roundLabel());
        assertEquals(List.of("king_of_spades", "king_of_hearts"), state.localPlayerCards());
        assertEquals(true, state.seats().getFirst().occupied());
        assertEquals(true, state.seats().get(1).occupied());
    }

    @Test
    void shouldKeepSeatOccupied_whenPlayerIsDisconnected() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of(),
                List.of("2_of_clubs", "2_of_diamonds"),
                List.of(
                        new BackendPlayerTransport(
                                "Nina",
                                980,
                                false,
                                false,
                                true,
                                true,
                                true,
                                true,
                                List.of("2_of_clubs", "2_of_diamonds")
                        ),
                        new BackendPlayerTransport(
                                "Leo",
                                970,
                                true,
                                true,
                                false,
                                false,
                                false,
                                true,
                                List.of()
                        )
                )
        );
        BackendTableStateAdapter adapter = new BackendTableStateAdapter();

        // Act
        TableState state = adapter.toTableState(payload);

        // Assert
        assertEquals(true, state.seats().get(1).occupied());
    }

    @Test
    void shouldUseSingleNonEmptyPocket_whenCurrentPlayerFlagIsMissing() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of(),
                List.of(),
                List.of(
                        new BackendPlayerTransport(
                                "Nina",
                                980,
                                false,
                                false,
                                null,
                                true,
                                true,
                                true,
                                List.of("ace_of_spades", "ace_of_hearts")
                        ),
                        new BackendPlayerTransport(
                                "Leo",
                                970,
                                true,
                                true,
                                null,
                                true,
                                true,
                                true,
                                List.of()
                        )
                )
        );
        BackendTableStateAdapter adapter = new BackendTableStateAdapter();

        // Act
        TableState state = adapter.toTableState(payload);

        // Assert
        assertEquals(List.of("ace_of_spades", "ace_of_hearts"), state.localPlayerCards());
    }
}




