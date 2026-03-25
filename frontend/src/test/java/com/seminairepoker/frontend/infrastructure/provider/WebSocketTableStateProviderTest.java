package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.model.TableState;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebSocketTableStateProviderTest {

    @Test
    void shouldReturnBackendState_whenWebSocketClientReturnsValidTableStatePayload() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        List<String> capturedRequests = new ArrayList<>();

        String responsePayload = """
                {
                  "type":"table_state",
                  "tableId":"AB123",
                  "roundLabel":"River",
                  "pot":1337,
                  "communityCards":["10_of_hearts","jack_of_hearts","queen_of_hearts","king_of_hearts","ace_of_hearts"],
                  "localPlayerCards":["2_of_clubs","2_of_diamonds"],
                  "seats":[
                    {"seatIndex":1,"playerName":"Nina","stack":1540,"dealer":false,"occupied":true,"acting":false},
                    {"seatIndex":2,"playerName":"Leo","stack":2240,"dealer":true,"occupied":true,"acting":true}
                  ]
                }
                """;

        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) -> {
            capturedRequests.add(requestMessage);
            assertEquals(endpoint, uri);
            assertEquals(timeout, requestTimeout);
            if ("ping".equals(requestMessage)) {
                return "pong";
            }
            return responsePayload;
        };

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(endpoint, timeout, messageClient);

        // Act
        TableState state = provider.loadInitialState();

        // Assert
        assertEquals("AB123", state.tableCode());
        assertEquals("River", state.roundLabel());
        assertEquals(1337, state.pot());
        assertEquals(5, state.communityCards().size());
        assertEquals(2, state.localPlayerCards().size());
        assertEquals(2, state.seats().size());
        assertEquals(List.of("ping", "{\"type\":\"get_table_state\"}"), capturedRequests);
    }

    @Test
    void shouldThrowException_whenWebSocketClientFails() {
        // Arrange
        WebSocketMessageClient messageClient = (uri, requestMessage, timeout) -> {
            throw new IllegalStateException("timeout");
        };

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                messageClient
        );

        // Act + Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, provider::loadInitialState);
        assertEquals("Unable to load table state from backend websocket", exception.getMessage());
    }

    @Test
    void shouldThrowException_whenPayloadIsNotATableStateMessage() {
        // Arrange
        WebSocketMessageClient messageClient = (uri, requestMessage, timeout) ->
                "ping".equals(requestMessage) ? "pong" : "{\"type\":\"pong\"}";

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                messageClient
        );

        // Act + Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, provider::loadInitialState);
        assertEquals("Unable to load table state from backend websocket", exception.getMessage());
    }

    @Test
    void shouldThrowException_whenPingResponseIsNotPong() {
        // Arrange
        AtomicReference<String> capturedRequest = new AtomicReference<>();
        WebSocketMessageClient messageClient = (uri, requestMessage, timeout) -> {
            capturedRequest.set(requestMessage);
            return "not-pong";
        };

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                messageClient
        );

        // Act + Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, provider::loadInitialState);
        assertEquals("Unable to load table state from backend websocket", exception.getMessage());
        assertEquals("ping", capturedRequest.get());
    }
}
