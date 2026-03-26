package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.Node;
import javafx.scene.Parent;
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
        PlayerSeatUiState state = new PlayerSeatUiState(2, "Nina", 1_540, true, true, true, false, true);

        // Act
        PlayerSeatView seatView = onFxThread(() -> new PlayerSeatView(state));
        List<String> texts = onFxThread(() -> List.of(
                ((Label) seatView.getChildren().get(0)).getText(),
                ((Label) seatView.getChildren().get(1)).getText(),
                ((Label) findByStyleClass(seatView, "seat-role")).getText()
        ));

        // Assert
        assertAll(
                () -> assertEquals("Nina", texts.get(0)),
                () -> assertEquals("1540EUR", normalizeCurrency(texts.get(1))),
                () -> assertEquals("D", texts.get(2)),
                () -> assertTrue(seatView.getStyleClass().contains("seat-acting")),
                () -> assertTrue(findReadyIndicator(seatView).getStyleClass().contains("seat-ready"))
        );
    }

    @Test
    void shouldDisplayEmptySeat_whenSeatIsNotOccupied() throws Exception {
        // Arrange
        PlayerSeatUiState state = new PlayerSeatUiState(5, "", 0, false, false, false, false, false);

        // Act
        PlayerSeatView seatView = onFxThread(() -> new PlayerSeatView(state));
        List<String> texts = onFxThread(() -> List.of(
                ((Label) seatView.getChildren().get(0)).getText(),
                ((Label) seatView.getChildren().get(1)).getText(),
                ((Label) findByStyleClass(seatView, "seat-role")).getText()
        ));

        // Assert
        assertAll(
                () -> assertEquals("Empty", texts.get(0)),
                () -> assertEquals("-", texts.get(1)),
                () -> assertEquals("", texts.get(2)),
                () -> assertFalse(seatView.getStyleClass().contains("seat-acting")),
                () -> assertFalse(findReadyIndicator(seatView).isVisible())
        );
    }

    @Test
    void shouldShowNotReadyIndicator_whenSeatIsOccupiedAndNotReady() throws Exception {
        // Arrange
        PlayerSeatUiState state = new PlayerSeatUiState(1, "Leo", 950, false, true, false, false, false);

        // Act
        PlayerSeatView seatView = onFxThread(() -> new PlayerSeatView(state));

        // Assert
        assertTrue(findReadyIndicator(seatView).getStyleClass().contains("seat-not-ready"));
    }

    private String normalizeCurrency(String text) {
        return text
                .replace("€", "EUR")
                .replaceAll("[\\s\\u00A0\\u202F]", "");
    }

    private Node findReadyIndicator(PlayerSeatView seatView) {
        return findByStyleClass(seatView, "seat-ready-indicator");
    }

    private Node findByStyleClass(Node node, String styleClass) {
        if (node.getStyleClass().contains(styleClass)) {
            return node;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Node nested = findByStyleClass(child, styleClass);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }
}

