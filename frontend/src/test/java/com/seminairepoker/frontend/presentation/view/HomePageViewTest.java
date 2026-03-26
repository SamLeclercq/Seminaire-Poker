package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.HomePageUiState;
import com.seminairepoker.frontend.presentation.state.JoinTableFormUiState;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomePageViewTest extends FxUiTestSupport {

    @Test
    void shouldShowJoinForm_whenJoinActionIsRequested() throws Exception {
        // Arrange
        HomePageView homePageView = onFxThread(() -> new HomePageView(sampleState(false)));

        // Act
        boolean isFormVisible = onFxThread(() -> {
            homePageView.getJoinTableButton().fire();
            return homePageView.getJoinTableFormView().isFormVisible();
        });

        // Assert
        assertTrue(isFormVisible);
    }

    @Test
    void shouldTriggerCreateCallback_whenCreateActionIsRequested() throws Exception {
        // Arrange
        HomePageView homePageView = onFxThread(() -> new HomePageView(sampleState(false)));
        AtomicBoolean callbackTriggered = new AtomicBoolean(false);
        onFxThread(() -> {
            homePageView.setOnCreateTableRequested(() -> callbackTriggered.set(true));
            return null;
        });

        // Act
        onFxThread(() -> {
            homePageView.getCreateTableButton().fire();
            return null;
        });

        // Assert
        assertTrue(callbackTriggered.get());
    }

    @Test
    void shouldHideJoinForm_whenNotRequested() throws Exception {
        // Arrange
        HomePageView homePageView = onFxThread(() -> new HomePageView(sampleState(false)));

        // Act
        boolean isFormVisible = onFxThread(() -> homePageView.getJoinTableFormView().isFormVisible());

        // Assert
        assertFalse(isFormVisible);
    }

    private HomePageUiState sampleState(boolean joinFormVisible) {
        return new HomePageUiState(
                "Seminaire Poker",
                "Choisissez votre table",
                "Creer une table",
                "Rejoindre une table",
                new JoinTableFormUiState(joinFormVisible, "Code table", "Rejoindre", "")
        );
    }
}

