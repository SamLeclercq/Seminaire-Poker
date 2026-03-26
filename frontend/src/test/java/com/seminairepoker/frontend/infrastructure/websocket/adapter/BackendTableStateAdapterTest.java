package com.seminairepoker.frontend.infrastructure.websocket.adapter;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendPlayerTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BackendTableStateAdapterTest {

    @Test
    void shouldPreferRevealedCurrentPlayerPocket_whenTopLevelPocketIsFaceDown() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of(),
                List.of("card_face_down", "card_face_down"),
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

    @Test
    void shouldNormalizeObjectCardsAndBackAlias_whenMappingPayload() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of("back", Map.of("rank", "King", "suit", "Hearts")),
                List.of("back", "back"),
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
                                List.of(
                                        Map.of("rank", "Ace", "suit", "Spades"),
                                        Map.of("rank", "10", "suit", "diamonds")
                                )
                        )
                )
        );
        BackendTableStateAdapter adapter = new BackendTableStateAdapter();

        // Act
        TableState state = adapter.toTableState(payload);

        // Assert
        assertEquals(List.of("ace_of_spades", "10_of_diamonds"), state.localPlayerCards());
        assertEquals(List.of("card_face_down", "king_of_hearts"), state.communityCards());
    }

    @Test
    void shouldMapTwoCardsPerSeat_whenBackendPocketIsMissingOrHidden() {
        // Arrange
        BackendTableStatePayloadTransport payload = new BackendTableStatePayloadTransport(
                "AB123",
                "preflop",
                1,
                30,
                List.of(),
                List.of("ace_of_spades", "ace_of_hearts"),
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
        assertEquals(List.of("ace_of_spades", "ace_of_hearts"), state.seats().getFirst().cards());
        assertEquals(List.of("card_face_down", "card_face_down"), state.seats().get(1).cards());
    }

    @Test
    void shouldNormalizeSpelledRanksFromBackendCardRepresentation() {
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
                                true,
                                true,
                                true,
                                true,
                                true,
                                List.of("two_of_spades", "nine_of_hearts")
                        )
                )
        );
        BackendTableStateAdapter adapter = new BackendTableStateAdapter();

        // Act
        TableState state = adapter.toTableState(payload);

        // Assert
        assertEquals(List.of("2_of_spades", "9_of_hearts"), state.localPlayerCards());
        assertEquals(List.of("2_of_spades", "9_of_hearts"), state.seats().getFirst().cards());
    }
}




