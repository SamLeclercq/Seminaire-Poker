package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.DisconnectPlayerPort;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DisconnectPlayerServiceTest {

    @Test
    void should_disconnect_player_when_disconnect_is_requested() {
        // Arrange
        AtomicBoolean disconnectCalled = new AtomicBoolean(false);
        DisconnectPlayerPort disconnectPlayerPort = () -> disconnectCalled.set(true);
        DisconnectPlayerService disconnectPlayerService = new DisconnectPlayerService(disconnectPlayerPort);

        // Act
        disconnectPlayerService.disconnectPlayer();

        // Assert
        assertTrue(disconnectCalled.get());
    }
}

