package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokerTableViewTest extends FxUiTestSupport {

    @Test
    void shouldShowTableIdentifier_whenTableIsRendered() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false))
        );

        // Act
        String headerText = onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader());
            return findLabelByStyleClass(pokerTableView, "table-id-label").getText();
        });

        // Assert
        assertEquals("Table AB123", headerText);
    }

    @Test
    void shouldTriggerReturnHomeCallback_whenReturnHomeButtonIsClicked() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false))
        );
        AtomicBoolean callbackTriggered = new AtomicBoolean(false);

        // Act
        onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader(), () -> callbackTriggered.set(true));
            findButtonByText(pokerTableView, "Retour accueil").fire();
            return null;
        });

        // Assert
        assertTrue(callbackTriggered.get());
    }

    private Label findLabelByStyleClass(Node node, String styleClass) {
        if (node instanceof Label label && label.getStyleClass().contains(styleClass)) {
            return label;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label nested = findLabelByStyleClass(child, styleClass);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private Button findButtonByText(Node node, String text) {
        if (node instanceof Button button && text.equals(button.getText())) {
            return button;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Button nested = findButtonByText(child, text);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }
}
