package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketReadyProviderTest {

    @Test
    void shouldReturnTrue_whenBackendReturnsGameStateAfterReadyRequest() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"game_state\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"preflop\",\"pot\":15,\"players\":[]}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketReadyProvider readyProvider = new WebSocketReadyProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean ready = readyProvider.markReady("AB123");

        // Assert
        assertTrue(ready);
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"ready\",\"payload\":{\"tableId\":\"AB123\"}}"
        ), sessionClient.sentMessages());
    }

    @Test
    void shouldReturnTrue_whenBackendAcceptsLegacyReadyAction() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"ready\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"waiting\",\"pot\":0,\"players\":[]}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketReadyProvider readyProvider = new WebSocketReadyProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean ready = readyProvider.markReady("AB123");

        // Assert
        assertTrue(ready);
    }

    @Test
    void shouldReturnFalse_whenBackendRejectsReadyRequest() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"error\",\"message\":\"Table not found\"}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketReadyProvider readyProvider = new WebSocketReadyProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean ready = readyProvider.markReady("AB123");

        // Assert
        assertFalse(ready);
    }

    @Test
    void shouldReturnFalse_whenBackendReturnsUnexpectedSuccessAction() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"waiting\",\"pot\":0,\"players\":[]}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketReadyProvider readyProvider = new WebSocketReadyProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean ready = readyProvider.markReady("AB123");

        // Assert
        assertFalse(ready);
    }
}

