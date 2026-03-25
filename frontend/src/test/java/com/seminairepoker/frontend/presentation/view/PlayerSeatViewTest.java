package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerSeatViewTest extends FxUiTestSupport {

    @Test
    void shouldDisplayPlayerAndRole_whenSeatIsOccupiedDealerAndActing() throws Exception {
        // Arrange
        PlayerSeatUiState state = new PlayerSeatUiState(2, "Nina", 1_540, true, true, true);

        // Act
        PlayerSeatView seatView = onFxThread(() -> new PlayerSeatView(state));
        List<String> texts = onFxThread(() -> seatView.getChildren()
                .stream()
                .map(node -> ((Label) node).getText())
                .toList());

        // Assert
        assertAll(
                () -> assertEquals("Nina", texts.get(0)),
                () -> assertEquals("1540EUR", normalizeCurrency(texts.get(1))),
                () -> assertEquals("D", texts.get(2)),
                () -> assertTrue(seatView.getStyleClass().contains("seat-acting"))
        );
    }

    @Test
    void shouldDisplayEmptySeat_whenSeatIsNotOccupied() throws Exception {
        // Arrange
        PlayerSeatUiState state = new PlayerSeatUiState(5, "", 0, false, false, false);

        // Act
        PlayerSeatView seatView = onFxThread(() -> new PlayerSeatView(state));
        List<String> texts = onFxThread(() -> seatView.getChildren()
                .stream()
                .map(node -> ((Label) node).getText())
                .toList());

        // Assert
        assertAll(
                () -> assertEquals("Empty", texts.get(0)),
                () -> assertEquals("-", texts.get(1)),
                () -> assertEquals("", texts.get(2)),
                () -> assertFalse(seatView.getStyleClass().contains("seat-acting"))
        );
    }

    private String normalizeCurrency(String text) {
        return text
                .replace("€", "EUR")
                .replaceAll("[\\s\\u00A0\\u202F]", "");
    }
}

