package com.seminairepoker.frontend.infrastructure.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public final class BackendWebSocketSession {
    private static final String CONNECT_REQUEST_PREFIX = "{\"action\":\"connect\",\"payload\":{\"playerName\":\"";
    private static final String CONNECT_REQUEST_SUFFIX = "\"}}";

    private final URI endpointUri;
    private final Duration requestTimeout;
    private final WebSocketSessionClient sessionClient;
    private final ObjectMapper objectMapper;

    private BackendTableStatePayloadTransport lastKnownState;
    private boolean connected;

    public BackendWebSocketSession(URI endpointUri, Duration requestTimeout, WebSocketSessionClient sessionClient) {
        this.endpointUri = Objects.requireNonNull(endpointUri, "endpointUri must not be null");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
        this.sessionClient = Objects.requireNonNull(sessionClient, "sessionClient must not be null");
        this.objectMapper = new ObjectMapper();
    }

    synchronized void connect(String playerName) {
        if (connected) {
            return;
        }

        String request = CONNECT_REQUEST_PREFIX + escapeJson(playerName) + CONNECT_REQUEST_SUFFIX;
        try {
            if (!sessionClient.isOpen()) {
                sessionClient.open(endpointUri, requestTimeout);
            }
            String payload = sessionClient.sendAndAwait(request, requestTimeout);
            BackendEnvelope response = objectMapper.readValue(payload, BackendEnvelope.class);
            if (response == null || !"success".equals(response.status) || !"connect".equals(response.action)) {
                throw new IllegalArgumentException("Backend connect response is invalid");
            }
            connected = true;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to connect player through backend websocket", exception);
        }
    }

    synchronized BackendEnvelope sendAction(String requestMessage, String failureMessage) {
        if (!connected) {
            throw new IllegalStateException("Player must be connected before sending table actions");
        }

        try {
            String payload = sessionClient.sendAndAwait(requestMessage, requestTimeout);
            BackendEnvelope response = objectMapper.readValue(payload, BackendEnvelope.class);
            if (response != null && "success".equals(response.status)) {
                BackendTableStatePayloadTransport statePayload = resolvePayload(response);
                if (statePayload != null) {
                    lastKnownState = statePayload;
                }
            }
            return response;
        } catch (Exception exception) {
            throw new IllegalStateException(failureMessage, exception);
        }
    }

    synchronized BackendTableStatePayloadTransport requireLastKnownState() {
        if (lastKnownState == null) {
            throw new IllegalStateException("Unable to load table state before joining or creating a table");
        }
        return lastKnownState;
    }

    private BackendTableStatePayloadTransport resolvePayload(BackendEnvelope response) {
        if (response == null) {
            return null;
        }
        if (response.data != null) {
            return response.data;
        }

        boolean hasDirectGameState = response.tableId != null
                || response.currentState != null
                || response.pot != null
                || response.communityCards != null
                || response.playerPocket != null
                || response.players != null;

        if (!hasDirectGameState) {
            return null;
        }

        return new BackendTableStatePayloadTransport(
                response.tableId,
                response.currentState,
                response.pot,
                response.communityCards,
                response.playerPocket,
                response.players
        );
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class BackendEnvelope {
        public String status;
        public String action;
        public String message;
        public BackendTableStatePayloadTransport data;
        public String tableId;
        public String currentState;
        public Integer pot;
        public java.util.List<Object> communityCards;
        public java.util.List<Object> playerPocket;
        public java.util.List<BackendPlayerTransport> players;
    }
}


