package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;
import com.seminairepoker.frontend.application.port.DisconnectPlayerPort;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class WebSocketPlayerConnectionProvider implements ConnectPlayerPort, DisconnectPlayerPort {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "8765";

    private final URI endpointUri;
    private final Duration requestTimeout;
    private final WebSocketMessageClient messageClient;
    private final ObjectMapper objectMapper;

    public WebSocketPlayerConnectionProvider() {
        this(resolveEndpointUri(), DEFAULT_TIMEOUT, new JavaNetWebSocketMessageClient());
    }

    public WebSocketPlayerConnectionProvider(URI endpointUri, Duration requestTimeout, WebSocketMessageClient messageClient) {
        this.endpointUri = Objects.requireNonNull(endpointUri, "endpointUri must not be null");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
        this.messageClient = Objects.requireNonNull(messageClient, "messageClient must not be null");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void connectPlayer(String playerName) {
        String requestMessage = "{\"action\":\"connect\",\"payload\":{\"playerName\":\"" + escapeJson(playerName) + "\"}}";
        sendRequest(requestMessage, "Unable to connect player through backend websocket");
    }

    @Override
    public void disconnectPlayer() {
        // Backend handles disconnection on socket close; returning home only clears local UI state.
    }

    private void sendRequest(String requestMessage, String failureMessage) {
        try {
            String responsePayload = messageClient.request(endpointUri, requestMessage, requestTimeout);
            validateResponse(responsePayload);
        } catch (Exception exception) {
            throw new IllegalStateException(failureMessage, exception);
        }
    }

    private void validateResponse(String responsePayload) throws Exception {
        BackendResponse response = objectMapper.readValue(responsePayload, BackendResponse.class);
        if (response == null || response.status == null || !"success".equals(response.status)) {
            throw new IllegalArgumentException("Backend response status is not success");
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

    private static URI resolveEndpointUri() {
        String wsUrl = System.getenv("POKER_WS_URL");
        if (wsUrl != null && !wsUrl.isBlank()) {
            return URI.create(wsUrl);
        }

        String host = System.getenv().getOrDefault("POKER_WS_HOST", DEFAULT_HOST);
        String port = System.getenv().getOrDefault("POKER_WS_PORT", DEFAULT_PORT);
        return URI.create("ws://" + host + ":" + port);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class BackendResponse {
        public String status;
    }
}

