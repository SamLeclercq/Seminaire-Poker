package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketPlayerActionProviderTest {

    @Test
    void shouldSendCheckAndFoldRequests_withBackendContract() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"check\",\"data\":{\"tableId\":\"AB123\"}}",
                "{\"status\":\"success\",\"action\":\"fold\",\"data\":{\"tableId\":\"AB123\"}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketPlayerActionProvider actionProvider = new WebSocketPlayerActionProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean checked = actionProvider.check("AB123");
        boolean folded = actionProvider.fold("AB123");

        // Assert
        assertTrue(checked);
        assertTrue(folded);
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"check\",\"payload\":{\"tableId\":\"AB123\"}}",
                "{\"action\":\"fold\",\"payload\":{\"tableId\":\"AB123\"}}"
        ), sessionClient.sentMessages());
    }

    @Test
    void shouldSerializeAmountPayload_whenBetAndRaiseAreRequested() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"bet\",\"data\":{\"tableId\":\"AB123\"}}",
                "{\"status\":\"success\",\"action\":\"raise\",\"data\":{\"tableId\":\"AB123\"}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketPlayerActionProvider actionProvider = new WebSocketPlayerActionProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean bet = actionProvider.bet("AB123", 40);
        boolean raised = actionProvider.raise("AB123", 120);

        // Assert
        assertTrue(bet);
        assertTrue(raised);
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"bet\",\"payload\":{\"tableId\":\"AB123\",\"amount\":40}}",
                "{\"action\":\"raise\",\"payload\":{\"tableId\":\"AB123\",\"amount\":120}}"
        ), sessionClient.sentMessages());
    }

    @Test
    void shouldReturnFalse_whenBackendReturnsErrorStatusForAction() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"error\",\"message\":\"Invalid action\"}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketPlayerActionProvider actionProvider = new WebSocketPlayerActionProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean checked = actionProvider.check("AB123");

        // Assert
        assertFalse(checked);
    }
}

