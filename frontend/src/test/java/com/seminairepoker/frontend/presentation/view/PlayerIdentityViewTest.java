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
    void should_trigger_connect_callback_when_submit_action_is_requested() throws Exception {
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
    void should_show_validation_message_when_message_is_not_blank() throws Exception {
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
    void should_hide_validation_message_when_message_is_blank() throws Exception {
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

