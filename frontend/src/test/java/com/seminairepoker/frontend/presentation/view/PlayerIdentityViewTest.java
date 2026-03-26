package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.PlayerIdentityUiState;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerIdentityViewTest extends FxUiTestSupport {

    @Test
    void shouldTriggerConnectCallback_whenSubmitActionIsRequested() throws Exception {
        // Arrange
        PlayerIdentityView playerIdentityView = onFxThread(() -> new PlayerIdentityView(sampleState()));
        AtomicReference<String> capturedPlayerName = new AtomicReference<>();
        onFxThread(() -> {
            playerIdentityView.setOnConnectRequested(capturedPlayerName::set);
            playerIdentityView.getPlayerNameInput().setText("Nina");
            return null;
        });

        // Act
        onFxThread(() -> {
            playerIdentityView.getConnectButton().fire();
            return null;
        });

        // Assert
        assertEquals("Nina", capturedPlayerName.get());
    }

    @Test
    void shouldShowValidationMessage_whenMessageIsNotBlank() throws Exception {
        // Arrange
        PlayerIdentityView playerIdentityView = onFxThread(() -> new PlayerIdentityView(sampleState()));

        // Act
        boolean isValidationVisible = onFxThread(() -> {
            playerIdentityView.showValidationMessage("Prenom requis");
            return playerIdentityView.getValidationLabel().isVisible();
        });

        // Assert
        assertTrue(isValidationVisible);
    }

    @Test
    void shouldHideValidationMessage_whenMessageIsBlank() throws Exception {
        // Arrange
        PlayerIdentityView playerIdentityView = onFxThread(() -> new PlayerIdentityView(sampleState()));

        // Act
        boolean isValidationVisible = onFxThread(() -> {
            playerIdentityView.showValidationMessage("   ");
            return playerIdentityView.getValidationLabel().isVisible();
        });

        // Assert
        assertFalse(isValidationVisible);
    }

    private PlayerIdentityUiState sampleState() {
        return new PlayerIdentityUiState(
                "Seminaire Poker",
                "Entrez votre prenom pour rejoindre la room",
                "Votre prenom",
                "Continuer",
                ""
        );
    }
}

