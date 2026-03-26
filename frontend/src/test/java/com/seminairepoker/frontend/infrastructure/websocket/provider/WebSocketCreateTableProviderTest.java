package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebSocketCreateTableProviderTest {

    @Test
    void shouldReturnBackendTableId_whenCreateIsSentOnConnectedSocket() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"create\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"Waiting\",\"pot\":0,\"players\":[]}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketCreateTableProvider createTableProvider = new WebSocketCreateTableProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        String tableCode = createTableProvider.createTable();

        // Assert
        assertEquals("AB123", tableCode);
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"create\",\"payload\":{}}"
        ), sessionClient.sentMessages());
    }

    @Test
    void shouldThrowException_whenCreateResponseIsMissingCreateAcknowledgement() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"tableId\":\"AB123\",\"currentState\":\"Waiting\",\"pot\":0,\"players\":[]}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketCreateTableProvider createTableProvider = new WebSocketCreateTableProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        IllegalStateException exception = assertThrows(IllegalStateException.class, createTableProvider::createTable);

        // Assert
        assertEquals("Backend create response does not contain a tableId", exception.getMessage());
    }
}


