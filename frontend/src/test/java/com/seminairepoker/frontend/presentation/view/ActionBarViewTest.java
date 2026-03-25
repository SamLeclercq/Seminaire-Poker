package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.control.Button;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionBarViewTest extends FxUiTestSupport {

    @Test
    void shouldRenderActionsInExpectedOrder_whenConstructed() throws Exception {
        // Arrange

        // Act
        List<String> labels = onFxThread(() -> {
            ActionBarView actionBarView = new ActionBarView();
            return actionBarView.getChildren()
                    .stream()
                    .map(node -> (Button) node)
                    .map(Button::getText)
                    .toList();
        });

        // Assert
        assertEquals(List.of("Fold", "Check", "Call", "Raise"), labels);
    }

    @Test
    void shouldResizeAllButtons_whenSetButtonScale() throws Exception {
        // Arrange
        ActionBarView actionBarView = onFxThread(ActionBarView::new);

        // Act
        List<Double> widths = onFxThread(() -> {
            actionBarView.setButtonScale(1.5);
            return actionBarView.getChildren()
                    .stream()
                    .map(node -> (Button) node)
                    .map(Button::getPrefWidth)
                    .toList();
        });

        // Assert
        assertTrue(widths.stream().allMatch(width -> width.equals(210.0)));
    }
}

