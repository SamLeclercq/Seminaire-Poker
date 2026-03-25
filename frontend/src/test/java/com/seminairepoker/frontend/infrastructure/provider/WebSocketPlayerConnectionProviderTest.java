package com.seminairepoker.frontend.infrastructure.provider;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebSocketPlayerConnectionProviderTest {

    @Test
    void should_send_connect_request_with_player_name_when_connect_is_requested() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        List<String> capturedRequests = new ArrayList<>();

        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) -> {
            capturedRequests.add(requestMessage);
            assertEquals(endpoint, uri);
            assertEquals(timeout, requestTimeout);
            return "ok";
        };

        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(endpoint, timeout, messageClient);

        // Act
        provider.connectPlayer("Nina");

        // Assert
        assertEquals(List.of("{\"type\":\"connect\",\"playerName\":\"Nina\"}"), capturedRequests);
    }

    @Test
    void should_send_disconnect_request_when_disconnect_is_requested() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        List<String> capturedRequests = new ArrayList<>();

        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) -> {
            capturedRequests.add(requestMessage);
            assertEquals(endpoint, uri);
            assertEquals(timeout, requestTimeout);
            return "ok";
        };

        WebSocketPlayerConnectionProvider provider = new WebSocketPlayerConnectionProvider(endpoint, timeout, messageClient);

        // Act
        provider.disconnectPlayer();

        // Assert
        assertEquals(List.of("{\"type\":\"disconnect\"}"), capturedRequests);
    }
}

