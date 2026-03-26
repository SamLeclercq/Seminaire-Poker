package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.infrastructure.websocket.provider.WebSocketCreateTableProvider;
import com.seminairepoker.frontend.infrastructure.websocket.provider.WebSocketPlayerConnectionProvider;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebSocketPlayerConnectionProviderTest {

    @Test
    void should_send_connect_request_with_backend_contract_when_connect_is_requested() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(backendSession);

        // Act
        provider.connectPlayer("Nina");

        // Assert
        assertEquals(1, sessionClient.openCallCount());
        assertEquals(endpoint, sessionClient.connectedUri());
        assertEquals(timeout, sessionClient.connectedTimeout());
        assertEquals(List.of("{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}"), sessionClient.sentMessages());
    }

    @Test
    void should_throw_exception_when_backend_returns_error_on_connect() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"error\",\"message\":\"Connect action requires a 'playerName' in payload.\"}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(backendSession);

        // Act + Assert
        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> provider.connectPlayer("Nina")
        );
        assertEquals("Unable to connect player through backend websocket", exception.getMessage());
    }

    @Test
    void should_keep_socket_open_for_follow_up_actions_when_player_is_connected() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"create\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"Waiting\",\"pot\":0,\"players\":[]}}"
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketCreateTableProvider createTableProvider = new WebSocketCreateTableProvider(backendSession);

        // Act
        provider.connectPlayer("Nina");
        createTableProvider.createTable();

        // Assert
        assertEquals(1, sessionClient.openCallCount());
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"create\",\"payload\":{}}"
        ), sessionClient.sentMessages());
    }
}


