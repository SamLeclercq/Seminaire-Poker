package com.seminairepoker.frontend.infrastructure.provider;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebSocketPlayerConnectionProviderTest {

    @Test
    void should_send_connect_request_with_backend_contract_when_connect_is_requested() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        List<String> capturedRequests = new ArrayList<>();

        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) -> {
            capturedRequests.add(requestMessage);
            assertEquals(endpoint, uri);
            assertEquals(timeout, requestTimeout);
            return "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}";
        };

        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(endpoint, timeout, messageClient);

        // Act
        provider.connectPlayer("Nina");

        // Assert
        assertEquals(List.of("{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}"), capturedRequests);
    }

    @Test
    void should_throw_exception_when_backend_returns_error_on_connect() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) ->
                "{\"status\":\"error\",\"message\":\"Connect action requires a 'playerName' in payload.\"}";

        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(endpoint, timeout, messageClient);

        // Act + Assert
        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> provider.connectPlayer("Nina")
        );
        assertEquals("Unable to connect player through backend websocket", exception.getMessage());
    }

    @Test
    void should_not_send_disconnect_request_when_disconnect_is_requested() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        List<String> capturedRequests = new ArrayList<>();

        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) -> {
            capturedRequests.add(requestMessage);
            return "{\"status\":\"success\",\"action\":\"leave\",\"data\":{}}";
        };

        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(endpoint, timeout, messageClient);

        // Act
        provider.disconnectPlayer();

        // Assert
        assertEquals(List.of(), capturedRequests);
    }
}

