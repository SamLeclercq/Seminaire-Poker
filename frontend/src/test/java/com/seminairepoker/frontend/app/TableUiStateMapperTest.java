package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableUiStateMapperTest {

    @Test
    void shouldMapApplicationStateToUiState_whenMappingTableState() {
        // Arrange
        TableState tableState = new TableState(
                "AB123",
                "waiting",
                780,
                List.of("ace_of_spades", "king_of_spades", "queen_of_spades"),
                List.of("2_of_hearts", "2_of_clubs"),
                List.of(
                        new PlayerSeatState(1, "Nina", 1_200, true, true, false, true, true, List.of("2_of_hearts", "2_of_clubs")),
                        new PlayerSeatState(2, "Leo", 900, false, true, false, false, false, List.of("card_face_down", "card_face_down"))
                )
        );

        // Act
        TableUiState uiState = TableUiStateMapper.toUiState(tableState);

        // Assert
        assertEquals("AB123", uiState.tableCode());
        assertEquals("waiting", uiState.roundLabel());
        assertEquals(780, uiState.pot());
        assertEquals(3, uiState.communityCards().size());
        assertEquals(2, uiState.localPlayerCards().size());
        assertEquals(2, uiState.seats().size());
        assertEquals(true, uiState.waitingForReady());
        assertEquals(true, uiState.localPlayerReady());
        assertEquals("Nina", uiState.seats().getFirst().playerName());
        assertEquals(true, uiState.seats().getFirst().ready());
        assertEquals(List.of("2_of_hearts", "2_of_clubs"), uiState.seats().getFirst().cards());
    }
}

