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

        // Simulates a preflop push arriving before UI subscription is ready.
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

