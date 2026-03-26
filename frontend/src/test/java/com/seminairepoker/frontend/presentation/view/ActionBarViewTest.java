package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.control.Button;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals(List.of("Fold", "Check", "Call", "Raise", "Pret"), labels);
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

    @Test
    void shouldRenderReadyButtonAsEnabledRed_whenWaitingAndNotReady() throws Exception {
        // Arrange
        ActionBarView actionBarView = onFxThread(ActionBarView::new);

        // Act
        Button readyButton = onFxThread(() -> {
            actionBarView.applyReadyState(true, false);
            return actionBarView.readyButton();
        });

        // Assert
        assertEquals("Pret", readyButton.getText());
        assertTrue(readyButton.getStyleClass().contains("action-ready-pending"));
        assertTrue(readyButton.isDisable() == false);
    }

    @Test
    void shouldRenderReadyButtonAsDisabledGreen_whenLocalPlayerIsReady() throws Exception {
        // Arrange
        ActionBarView actionBarView = onFxThread(ActionBarView::new);

        // Act
        Button readyButton = onFxThread(() -> {
            actionBarView.applyReadyState(true, true);
            return actionBarView.readyButton();
        });

        // Assert
        assertTrue(readyButton.getStyleClass().contains("action-ready-active"));
        assertTrue(readyButton.isDisable());
    }

    @Test
    void shouldHideReadyButton_whenStateIsNotWaitingEvenIfLocalPlayerIsReady() throws Exception {
        // Arrange
        ActionBarView actionBarView = onFxThread(ActionBarView::new);

        // Act
        Button readyButton = onFxThread(() -> {
            actionBarView.applyReadyState(false, true);
            return actionBarView.readyButton();
        });

        // Assert
        assertTrue(readyButton.isDisable());
        assertFalse(readyButton.isVisible());
        assertFalse(readyButton.isManaged());
    }

    @Test
    void shouldInvokeCallback_whenReadyButtonIsClicked() throws Exception {
        // Arrange
        AtomicBoolean callbackCalled = new AtomicBoolean(false);

        // Act
        onFxThread(() -> {
            ActionBarView actionBarView = new ActionBarView(() -> callbackCalled.set(true));
            actionBarView.applyReadyState(true, false);
            actionBarView.readyButton().fire();
            return null;
        });

        // Assert
        assertTrue(callbackCalled.get());
    }
}

