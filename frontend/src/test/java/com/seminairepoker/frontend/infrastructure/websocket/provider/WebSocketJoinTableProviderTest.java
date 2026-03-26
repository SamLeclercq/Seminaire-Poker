package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.infrastructure.websocket.provider.WebSocketJoinTableProvider;
import com.seminairepoker.frontend.infrastructure.websocket.provider.WebSocketPlayerConnectionProvider;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketJoinTableProviderTest {

    @Test
    void should_return_true_when_backend_accepts_join_request() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"Waiting\",\"pot\":0,\"players\":[]}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketJoinTableProvider joinTableProvider = new WebSocketJoinTableProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean joined = joinTableProvider.joinTable("AB123");

        // Assert
        assertTrue(joined);
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"join\",\"payload\":{\"tableId\":\"AB123\"}}"
        ), sessionClient.sentMessages());
    }

    @Test
    void should_return_false_when_backend_rejects_join_request() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"error\",\"message\":\"Table `XXXXX` not found.\"}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketJoinTableProvider joinTableProvider = new WebSocketJoinTableProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean joined = joinTableProvider.joinTable("XXXXX");

        // Assert
        assertFalse(joined);
    }

    @Test
    void should_return_false_when_join_response_is_missing_join_acknowledgement() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"tableId\":\"AB123\",\"currentState\":\"Waiting\",\"pot\":0,\"players\":[]}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketJoinTableProvider joinTableProvider = new WebSocketJoinTableProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        boolean joined = joinTableProvider.joinTable("AB123");

        // Assert
        assertFalse(joined);
    }
}


