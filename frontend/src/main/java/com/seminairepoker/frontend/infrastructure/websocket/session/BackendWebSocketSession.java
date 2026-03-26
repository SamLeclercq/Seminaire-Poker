package com.seminairepoker.frontend.infrastructure.websocket.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminairepoker.frontend.infrastructure.websocket.client.WebSocketSessionClient;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendActionRequestTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStateTransport;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class BackendWebSocketSession {
    private static final String CONNECT_REQUEST_PREFIX = "{\"action\":\"connect\",\"payload\":{\"playerName\":\"";
    private static final String CONNECT_REQUEST_SUFFIX = "\"}}";

    private final URI endpointUri;
    private final Duration requestTimeout;
    private final WebSocketSessionClient sessionClient;
    private final ObjectMapper objectMapper;
    private final List<Consumer<BackendTableStatePayloadTransport>> stateListeners;

    private BackendTableStatePayloadTransport lastKnownState;
    private boolean connected;

    public BackendWebSocketSession(URI endpointUri, Duration requestTimeout, WebSocketSessionClient sessionClient) {
        this.endpointUri = Objects.requireNonNull(endpointUri, "endpointUri must not be null");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
        this.sessionClient = Objects.requireNonNull(sessionClient, "sessionClient must not be null");
        this.objectMapper = new ObjectMapper();
        this.stateListeners = new ArrayList<>();
        this.sessionClient.setPushMessageListener(this::handlePushMessage);
    }

    public synchronized void connect(String playerName) {
        if (connected) {
            return;
        }

        String request = CONNECT_REQUEST_PREFIX + escapeJson(playerName) + CONNECT_REQUEST_SUFFIX;
        try {
            if (!sessionClient.isOpen()) {
                sessionClient.open(endpointUri, requestTimeout);
            }
            String payload = sessionClient.sendAndAwait(request, requestTimeout);
            BackendTableStateTransport response = objectMapper.readValue(payload, BackendTableStateTransport.class);
            if (response == null || !"success".equals(response.status()) || !"connect".equals(response.action())) {
                throw new IllegalArgumentException("Backend connect response is invalid");
            }
            connected = true;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to connect player through backend websocket", exception);
        }
    }

    public synchronized BackendTableStateTransport sendAction(BackendActionRequestTransport request, String failureMessage) {
        Objects.requireNonNull(request, "request must not be null");
        return sendAction(serializeRequest(request), failureMessage);
    }

    public synchronized BackendTableStateTransport sendAction(String requestMessage, String failureMessage) {
        if (!connected) {
            throw new IllegalStateException("Player must be connected before sending table actions");
        }

        try {
            String payload = sessionClient.sendAndAwait(requestMessage, requestTimeout);
            BackendTableStateTransport response = objectMapper.readValue(payload, BackendTableStateTransport.class);
            if (response != null) {
                BackendTableStatePayloadTransport statePayload = response.resolveStatePayload();
                if (statePayload != null && (response.isSuccessStatus() || response.hasDirectStatePayload())) {
                    updateLastKnownState(statePayload);
                }
            }
            return response;
        } catch (Exception exception) {
            throw new IllegalStateException(failureMessage, exception);
        }
    }

    public synchronized BackendTableStatePayloadTransport requireLastKnownState() {
        if (lastKnownState == null) {
            throw new IllegalStateException("Unable to load table state before joining or creating a table");
        }
        return lastKnownState;
    }

    public synchronized Runnable subscribeToStateUpdates(Consumer<BackendTableStatePayloadTransport> onStateUpdated) {
        Objects.requireNonNull(onStateUpdated, "onStateUpdated must not be null");
        stateListeners.add(onStateUpdated);
        return () -> removeStateListener(onStateUpdated);
    }

    private synchronized void removeStateListener(Consumer<BackendTableStatePayloadTransport> onStateUpdated) {
        stateListeners.remove(onStateUpdated);
    }

    private void handlePushMessage(String payload) {
        try {
            BackendTableStateTransport response = objectMapper.readValue(payload, BackendTableStateTransport.class);
            if (response == null) {
                return;
            }

            BackendTableStatePayloadTransport statePayload = response.resolveStatePayload();
            if (statePayload != null && (response.isSuccessStatus() || response.hasDirectStatePayload())) {
                updateLastKnownState(statePayload);
            }
        } catch (Exception ignored) {
            // Ignore malformed or unsupported push payloads.
        }
    }

    private synchronized void updateLastKnownState(BackendTableStatePayloadTransport statePayload) {
        lastKnownState = statePayload;
        List<Consumer<BackendTableStatePayloadTransport>> listenersSnapshot = List.copyOf(stateListeners);
        for (Consumer<BackendTableStatePayloadTransport> listener : listenersSnapshot) {
            listener.accept(statePayload);
        }
    }

    private String serializeRequest(BackendActionRequestTransport request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize backend action request", exception);
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

}



