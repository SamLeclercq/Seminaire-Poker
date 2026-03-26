package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
            HBox actionsRow = (HBox) actionBarView.getChildren().getFirst();
            return actionsRow.getChildren()
                    .stream()
                    .map(node -> (Button) node)
                    .map(Button::getText)
                    .toList();
        });

        // Assert
        assertEquals(List.of("Fold", "Check", "Call", "Bet", "Raise", "Pret"), labels);
    }

    @Test
    void shouldResizeAllButtons_whenSetButtonScale() throws Exception {
        // Arrange
        ActionBarView actionBarView = onFxThread(ActionBarView::new);

        // Act
        List<Double> widths = onFxThread(() -> {
            actionBarView.setButtonScale(1.5);
            HBox actionsRow = (HBox) actionBarView.getChildren().getFirst();
            return actionsRow.getChildren()
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

    @Test
    void shouldEnableBetAndRaiseOnlyWhenLegalActionsAllowThem() throws Exception {
        // Arrange
        ActionBarView actionBarView = onFxThread(ActionBarView::new);

        // Act
        List<Boolean> disabledStates = onFxThread(() -> {
            actionBarView.applyActionState(List.of("check", "bet"), 10, 120);
            HBox actionsRow = (HBox) actionBarView.getChildren().getFirst();
            Button betButton = (Button) actionsRow.getChildren().get(3);
            Button raiseButton = (Button) actionsRow.getChildren().get(4);
            return List.of(betButton.isDisable(), raiseButton.isDisable());
        });

        // Assert
        assertEquals(List.of(false, true), disabledStates);
    }

    @Test
    void shouldSubmitMinimumBetImmediately_whenBetIsOpened() throws Exception {
        // Arrange
        AtomicInteger capturedBet = new AtomicInteger(-1);
        ActionBarView actionBarView = onFxThread(() -> new ActionBarView(
                () -> { },
                () -> { },
                () -> { },
                () -> { },
                capturedBet::set,
                amount -> { }
        ));

        // Act
        onFxThread(() -> {
            actionBarView.applyActionState(List.of("bet"), 40, 250);
            HBox actionsRow = (HBox) actionBarView.getChildren().getFirst();
            Button betButton = (Button) actionsRow.getChildren().get(3);
            betButton.fire();

            HBox amountRow = (HBox) actionBarView.getChildren().get(1);
            Button validateButton = (Button) amountRow.getChildren().get(3);
            validateButton.fire();
            return null;
        });

        // Assert
        assertEquals(100, capturedBet.get());
    }

    @Test
    void shouldSubmitRaiseAboveCurrentBetImmediately_whenRaiseIsOpened() throws Exception {
        // Arrange
        AtomicInteger capturedRaise = new AtomicInteger(-1);
        ActionBarView actionBarView = onFxThread(() -> new ActionBarView(
                () -> { },
                () -> { },
                () -> { },
                () -> { },
                amount -> { },
                capturedRaise::set
        ));

        // Act
        onFxThread(() -> {
            actionBarView.applyActionState(List.of("raise"), 160, 300);
            HBox actionsRow = (HBox) actionBarView.getChildren().getFirst();
            Button raiseButton = (Button) actionsRow.getChildren().get(4);
            raiseButton.fire();

            HBox amountRow = (HBox) actionBarView.getChildren().get(1);
            Button validateButton = (Button) amountRow.getChildren().get(3);
            validateButton.fire();
            return null;
        });

        // Assert
        assertEquals(161, capturedRaise.get());
    }
}

