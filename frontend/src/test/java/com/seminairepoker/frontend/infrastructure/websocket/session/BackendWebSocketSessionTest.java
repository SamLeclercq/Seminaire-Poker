package com.seminairepoker.frontend.infrastructure.websocket.session;

import com.seminairepoker.frontend.infrastructure.websocket.client.WebSocketSessionClient;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BackendWebSocketSessionTest {

    @Test
    void shouldReplayLastKnownStateWhenSubscriberRegistersAfterPushUpdate() {
        // Arrange
        FakeSessionClient sessionClient = new FakeSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"waiting\",\"pot\":0}}"
        ));
        BackendWebSocketSession session = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );

        session.connect("Nina");
        session.sendAction("{\"action\":\"join\",\"payload\":{\"tableId\":\"AB123\"}}", "join failed");

        sessionClient.emitPush("""
                {
                  "tableId":"AB123",
                  "currentState":"preflop",
                  "currentHand":1,
                  "pot":15,
                  "communityCards":[],
                  "players":[]
                }
                """);

        AtomicReference<BackendTableStatePayloadTransport> observed = new AtomicReference<>();

        // Act
        session.subscribeToStateUpdates(observed::set);

        // Assert
        assertNotNull(observed.get());
        assertEquals("preflop", observed.get().currentState());
    }

    @Test
    void shouldDeliverReplayAndFutureUpdatesToSameSubscriber() {
        // Arrange
        FakeSessionClient sessionClient = new FakeSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"waiting\",\"pot\":0}}"
        ));
        BackendWebSocketSession session = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );

        session.connect("Nina");
        session.sendAction("{\"action\":\"join\",\"payload\":{\"tableId\":\"AB123\"}}", "join failed");

        AtomicInteger callbacks = new AtomicInteger(0);
        AtomicReference<String> lastState = new AtomicReference<>("unknown");

        // Act
        session.subscribeToStateUpdates(payload -> {
            callbacks.incrementAndGet();
            lastState.set(payload.currentState());
        });
        sessionClient.emitPush("""
                {
                  "tableId":"AB123",
                  "currentState":"preflop",
                  "currentHand":1,
                  "pot":15,
                  "communityCards":[],
                  "players":[]
                }
                """);

        // Assert
        assertEquals(2, callbacks.get());
        assertEquals("preflop", lastState.get());
    }

    @Test
    void shouldIgnoreNonStateActionPayload_whenResponseContainsOnlyWinnings() {
        // Arrange
        FakeSessionClient sessionClient = new FakeSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"preflop\",\"pot\":150,\"communityCards\":[\"card_face_down\"],\"players\":[]}}",
                "{\"status\":\"success\",\"action\":\"check\",\"data\":{\"winnings\":{\"player-1\":150}}}"
        ));
        BackendWebSocketSession session = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );

        session.connect("Nina");
        session.sendAction("{\"action\":\"join\",\"payload\":{\"tableId\":\"AB123\"}}", "join failed");

        // Act
        session.sendAction("{\"action\":\"check\",\"payload\":{\"tableId\":\"AB123\"}}", "check failed");
        BackendTableStatePayloadTransport stateAfterAction = session.requireLastKnownState();

        // Assert
        assertEquals("preflop", stateAfterAction.currentState());
        assertEquals(150, stateAfterAction.pot());
        assertEquals(List.of("card_face_down"), stateAfterAction.communityCards());
    }

    @Test
    void shouldMergePartialPushWithLastKnownState_whenPayloadOmitsBoardFields() {
        // Arrange
        FakeSessionClient sessionClient = new FakeSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"flop\",\"currentHand\":1,\"pot\":220,\"communityCards\":[\"ace_of_spades\",\"king_of_spades\",\"2_of_hearts\"],\"players\":[]}}"
        ));
        BackendWebSocketSession session = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );

        session.connect("Nina");
        session.sendAction("{\"action\":\"join\",\"payload\":{\"tableId\":\"AB123\"}}", "join failed");

        // Act
        sessionClient.emitPush("{\"status\":\"success\",\"action\":\"tick\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"turn\"}}");
        BackendTableStatePayloadTransport mergedState = session.requireLastKnownState();

        // Assert
        assertEquals("turn", mergedState.currentState());
        assertEquals(220, mergedState.pot());
        assertEquals(List.of("ace_of_spades", "king_of_spades", "2_of_hearts"), mergedState.communityCards());
    }

    @Test
    void shouldIgnoreStaleRoundUpdate_whenOlderPhaseArrivesAfterNewerState() {
        // Arrange
        FakeSessionClient sessionClient = new FakeSessionClient(List.of(
                "{\"status\":\"success\",\"action\":\"connect\",\"data\":{}}",
                "{\"status\":\"success\",\"action\":\"join\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"turn\",\"currentHand\":2,\"pot\":320,\"communityCards\":[\"ace_of_spades\",\"king_of_spades\",\"2_of_hearts\",\"9_of_clubs\"],\"players\":[]}}"
        ));
        BackendWebSocketSession session = new BackendWebSocketSession(
                URI.create("ws://127.0.0.1:8765"),
                Duration.ofSeconds(2),
                sessionClient
        );

        session.connect("Nina");
        session.sendAction("{\"action\":\"join\",\"payload\":{\"tableId\":\"AB123\"}}", "join failed");

        // Act
        sessionClient.emitPush("{\"status\":\"success\",\"action\":\"tick\",\"data\":{\"tableId\":\"AB123\",\"currentState\":\"flop\",\"currentHand\":2,\"communityCards\":[\"ace_of_spades\",\"king_of_spades\",\"2_of_hearts\"]}}"
        );
        BackendTableStatePayloadTransport stateAfterStalePush = session.requireLastKnownState();

        // Assert
        assertEquals("turn", stateAfterStalePush.currentState());
        assertEquals(320, stateAfterStalePush.pot());
        assertEquals(List.of("ace_of_spades", "king_of_spades", "2_of_hearts", "9_of_clubs"), stateAfterStalePush.communityCards());
    }

    private static final class FakeSessionClient implements WebSocketSessionClient {
        private final Queue<String> responses;
        private Consumer<String> pushListener = payload -> { };
        private boolean open;

        private FakeSessionClient(List<String> responses) {
            this.responses = new ArrayDeque<>(responses);
        }

        @Override
        public void open(URI endpointUri, Duration timeout) {
            open = true;
        }

        @Override
        public String sendAndAwait(String message, Duration timeout) {
            if (responses.isEmpty()) {
                throw new IllegalStateException("No fake response configured");
            }
            return responses.remove();
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void setPushMessageListener(Consumer<String> pushMessageListener) {
            this.pushListener = pushMessageListener == null ? payload -> { } : pushMessageListener;
        }

        @Override
        public void close(Duration timeout) {
            open = false;
        }

        private void emitPush(String payload) {
            pushListener.accept(payload);
        }
    }
}

