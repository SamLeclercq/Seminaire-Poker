package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebSocketTableStateProviderTest {

    @Test
    void shouldLoadTableState_whenJoinReceivesDirectPayloadWithoutStatusEnvelope() {
        // Arrange
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                """
                        {
                          "tableId":"N2T53",
                          "currentState":"Turn",
                          "pot":460,
                          "communityCards":["ace_of_spades"],
                          "playerPocket":["2_of_clubs","2_of_diamonds"],
                          "players":[
                            {"playerName":"Alice","balance":900,"isDealer":true,"isInTurn":false},
                            {"playerName":"Bob","balance":1200,"isDealer":false,"isInTurn":true}
                          ]
                        }
                        """
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketJoinTableProvider joinTableProvider = new WebSocketJoinTableProvider(backendSession);
        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Alice");
        boolean joined = joinTableProvider.joinTable("N2T53");
        TableState tableState = provider.loadInitialState();

        // Assert
        assertFalse(joined);
        assertEquals("N2T53", tableState.tableCode());
        assertEquals("Turn", tableState.roundLabel());
        assertEquals(460, tableState.pot());
        assertEquals(1, tableState.communityCards().size());
        assertEquals(2, tableState.localPlayerCards().size());
        assertEquals(2, tableState.seats().size());
    }

    @Test
    void shouldReturnLastTableState_whenTableHasBeenCreated() {
        // Arrange
        URI endpoint = URI.create("ws://127.0.0.1:8765");
        Duration timeout = Duration.ofSeconds(2);
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                """
                        {
                          "status":"success",
                          "action":"create",
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
                        """
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(endpoint, timeout, sessionClient);
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketCreateTableProvider createTableProvider = new WebSocketCreateTableProvider(backendSession);
        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        createTableProvider.createTable();
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
        assertEquals(List.of(
                "{\"action\":\"connect\",\"payload\":{\"playerName\":\"Nina\"}}",
                "{\"action\":\"create\",\"payload\":{}}"
        ), sessionClient.sentMessages());
    }

    @Test
    void shouldUpdatePlayerSeats_whenBackendMessageContainsPlayers() {
        // Arrange
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                """
                        {
                          "status":"success",
                          "action":"join",
                          "data":{
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
                        }
                        """
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketJoinTableProvider joinTableProvider = new WebSocketJoinTableProvider(backendSession);
        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Alice");
        joinTableProvider.joinTable("N2T53");
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
    void shouldIgnoreMissingOptionalFields_whenBackendPayloadIsPartial() {
        // Arrange
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                """
                        {
                          "status":"success",
                          "action":"create",
                          "data":{
                            "tableId":"Q1W2E",
                            "currentState":"Waiting",
                            "pot":0,
                            "players":[
                              {"playerName":"Nina","balance":1000,"isCurrentPlayer":true,"isReady":true}
                            ]
                          }
                        }
                        """
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketCreateTableProvider createTableProvider = new WebSocketCreateTableProvider(backendSession);
        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        createTableProvider.createTable();
        TableState state = provider.loadInitialState();

        // Assert
        assertEquals("Q1W2E", state.tableCode());
        assertEquals("Waiting", state.roundLabel());
        assertEquals(0, state.pot());
        assertEquals(List.of(), state.communityCards());
        assertEquals(List.of(), state.localPlayerCards());
        assertEquals(1, state.seats().size());
        assertEquals("Nina", state.seats().getFirst().playerName());
        assertEquals(true, state.seats().getFirst().currentPlayer());
        assertEquals(true, state.seats().getFirst().ready());
    }

    @Test
    void shouldUseCurrentHand_whenCurrentStateIsMissingInPayload() {
        // Arrange
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                """
                        {
                          "status":"success",
                          "action":"create",
                          "data":{
                            "tableId":"Q1W2E",
                            "currentHand":"Pre-flop",
                            "pot":0,
                            "players":[
                              {"playerName":"Nina","balance":1000}
                            ]
                          }
                        }
                        """
        ));
        BackendWebSocketSession backendSession = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );
        WebSocketPlayerConnectionProvider connectionProvider = new WebSocketPlayerConnectionProvider(backendSession);
        WebSocketCreateTableProvider createTableProvider = new WebSocketCreateTableProvider(backendSession);
        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(backendSession);

        // Act
        connectionProvider.connectPlayer("Nina");
        createTableProvider.createTable();
        TableState state = provider.loadInitialState();

        // Assert
        assertEquals("Pre-flop", state.roundLabel());
    }

    @Test
    void shouldThrowException_whenNoStateHasBeenReceivedYet() {
        // Arrange
        FakeWebSocketSessionClient sessionClient = new FakeWebSocketSessionClient(List.of());
        BackendWebSocketSession backendSession = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );
        WebSocketTableStateProvider provider = new WebSocketTableStateProvider(backendSession);

        // Act + Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, provider::loadInitialState);
        assertEquals("Unable to load table state before joining or creating a table", exception.getMessage());
    }
}

