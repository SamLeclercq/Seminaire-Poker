package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.infrastructure.websocket.client.WebSocketSessionClient;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

final class FakeWebSocketSessionClient implements WebSocketSessionClient {
    private final Queue<String> responses;
    private final List<String> sentMessages;

    private URI connectedUri;
    private Duration connectedTimeout;
    private int openCallCount;
    private boolean open;
    private Consumer<String> pushMessageListener;

    FakeWebSocketSessionClient(List<String> responses) {
        this.responses = new ArrayDeque<>(responses);
        this.sentMessages = new ArrayList<>();
        this.pushMessageListener = message -> { };
    }

    @Override
    public void open(URI endpointUri, Duration timeout) {
        connectedUri = endpointUri;
        connectedTimeout = timeout;
        openCallCount++;
        open = true;
    }

    @Override
    public String sendAndAwait(String message, Duration timeout) {
        sentMessages.add(message);
        if (responses.isEmpty()) {
            throw new IllegalStateException("No fake websocket response configured");
        }
        return responses.remove();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setPushMessageListener(Consumer<String> pushMessageListener) {
        this.pushMessageListener = pushMessageListener == null ? message -> { } : pushMessageListener;
    }

    @Override
    public void close(Duration timeout) {
        open = false;
    }

    URI connectedUri() {
        return connectedUri;
    }

    Duration connectedTimeout() {
        return connectedTimeout;
    }

    int openCallCount() {
        return openCallCount;
    }

    List<String> sentMessages() {
        return List.copyOf(sentMessages);
    }

    void emitPush(String payload) {
        pushMessageListener.accept(payload);
    }
}


