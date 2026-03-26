package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.model.TableState;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebSocketTableStateProviderTest {

    @Test
    void should_map_backend_table_message_to_table_ui_state_when_payload_is_valid() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        List<String> capturedRequests = new ArrayList<>();

        String responsePayload = """
                {
                  "status":"success",
                  "action":"join",
                  "data":{
                    "tableId":"AB123",
                    "currentState":"River",
                    "pot":1337,
                    "communityCards":["10_of_hearts","jack_of_hearts","queen_of_hearts","king_of_hearts","ace_of_hearts"],
                    "playerPocket":["2_of_clubs","2_of_diamonds"],
                    "players":[
                      {"playerName":"Nina","balance":1540,"isDealer":false,"isInTurn":false},
                      {"playerName":"Leo","balance":2240,"isDealer":true,"isInTurn":true}
                    ]
                  }
                }
                """;

        WebSocketMessageClient messageClient = (uri, requestMessage, requestTimeout) -> {
            capturedRequests.add(requestMessage);
            assertEquals(endpoint, uri);
            assertEquals(timeout, requestTimeout);
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
        assertEquals("Nina", state.seats().getFirst().playerName());
        assertEquals(1540, state.seats().getFirst().stack());
        assertEquals("Leo", state.seats().get(1).playerName());
        assertEquals(true, state.seats().get(1).dealer());
        assertEquals(true, state.seats().get(1).acting());
        assertEquals(List.of("{\"action\":\"table_state\",\"payload\":{}}"), capturedRequests);
    }

    @Test
    void should_update_player_seats_when_backend_message_contains_players() {
        // Arrange
        String backendPushPayload = """
                {
                  "tableId":"N2T53",
                  "currentState":"Turn",
                  "pot":460,
                  "communityCards":["ace_of_spades"],
                  "playerPocket":[],
                  "players":[
                    {"playerName":"Alice","balance":900,"isDealer":true,"isInTurn":false},
                    {"playerName":"Bob","balance":1200,"isDealer":false,"isInTurn":true}
                  ]
                }
                """;

        WebSocketMessageClient messageClient = (uri, requestMessage, timeout) -> backendPushPayload;

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                messageClient
        );

        // Act
        TableState tableState = provider.loadInitialState();

        // Assert
        assertEquals(2, tableState.seats().size());
        assertEquals(1, tableState.seats().getFirst().seatIndex());
        assertEquals("Alice", tableState.seats().getFirst().playerName());
        assertEquals(2, tableState.seats().get(1).seatIndex());
        assertEquals("Bob", tableState.seats().get(1).playerName());
        assertEquals(true, tableState.seats().get(1).acting());
    }

    @Test
    void should_ignore_missing_optional_fields_when_backend_payload_is_partial() {
        // Arrange
        String partialPayload = """
                {
                  "status":"success",
                  "action":"create",
                  "data":{
                    "tableId":"Q1W2E",
                    "currentState":"Waiting",
                    "pot":0,
                    "players":[
                      {"playerName":"Nina","balance":1000}
                    ]
                  }
                }
                """;

        WebSocketMessageClient messageClient = (uri, requestMessage, timeout) -> partialPayload;

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                messageClient
        );

        // Act
        TableState state = provider.loadInitialState();

        // Assert
        assertEquals("Q1W2E", state.tableCode());
        assertEquals("Waiting", state.roundLabel());
        assertEquals(0, state.pot());
        assertEquals(List.of(), state.communityCards());
        assertEquals(List.of(), state.localPlayerCards());
        assertEquals(1, state.seats().size());
        assertEquals("Nina", state.seats().getFirst().playerName());
    }

    @Test
    void should_throw_exception_when_backend_returns_error_status() {
        // Arrange
        WebSocketMessageClient messageClient = (uri, requestMessage, timeout) ->
                "{\"status\":\"error\",\"message\":\"Table `XXXXX` not found.\"}";

        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                messageClient
        );

        // Act + Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, provider::loadInitialState);
        assertEquals("Unable to load table state from backend websocket", exception.getMessage());
    }
}
