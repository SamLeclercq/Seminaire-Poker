package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectPlayerServiceTest {

    @Test
    void should_connect_player_when_name_is_valid() {
        // Arrange
        AtomicReference<String> capturedPlayerName = new AtomicReference<>();
        ConnectPlayerPort connectPlayerPort = capturedPlayerName::set;
        ConnectPlayerService connectPlayerService = new ConnectPlayerService(connectPlayerPort, new PlayerNameValidator());

        // Act
        boolean isConnected = connectPlayerService.connectPlayer("  Nina  ");

        // Assert
        assertTrue(isConnected);
        assertEquals("Nina", capturedPlayerName.get());
    }

    @Test
    void should_reject_connection_when_player_name_is_invalid() {
        // Arrange
        AtomicReference<String> capturedPlayerName = new AtomicReference<>();
        ConnectPlayerPort connectPlayerPort = capturedPlayerName::set;
        ConnectPlayerService connectPlayerService = new ConnectPlayerService(connectPlayerPort, new PlayerNameValidator());

        // Act
        boolean isConnected = connectPlayerService.connectPlayer("  ");

        // Assert
        assertFalse(isConnected);
        assertEquals(null, capturedPlayerName.get());
    }
}

