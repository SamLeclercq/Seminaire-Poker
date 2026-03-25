package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;
import com.seminairepoker.frontend.application.port.DisconnectPlayerPort;

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

    public WebSocketPlayerConnectionProvider() {
        this(resolveEndpointUri(), DEFAULT_TIMEOUT, new JavaNetWebSocketMessageClient());
    }

    public WebSocketPlayerConnectionProvider(URI endpointUri, Duration requestTimeout, WebSocketMessageClient messageClient) {
        this.endpointUri = Objects.requireNonNull(endpointUri, "endpointUri must not be null");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
        this.messageClient = Objects.requireNonNull(messageClient, "messageClient must not be null");
    }

    @Override
    public void connectPlayer(String playerName) {
        String requestMessage = "{\"type\":\"connect\",\"playerName\":\"" + escapeJson(playerName) + "\"}";
        sendRequest(requestMessage, "Unable to connect player through backend websocket");
    }

    @Override
    public void disconnectPlayer() {
        sendRequest("{\"type\":\"disconnect\"}", "Unable to disconnect player through backend websocket");
    }

    private void sendRequest(String requestMessage, String failureMessage) {
        try {
            messageClient.request(endpointUri, requestMessage, requestTimeout);
        } catch (Exception exception) {
            throw new IllegalStateException(failureMessage, exception);
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
}

