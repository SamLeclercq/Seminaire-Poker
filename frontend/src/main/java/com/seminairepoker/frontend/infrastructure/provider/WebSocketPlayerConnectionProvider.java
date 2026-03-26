package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class WebSocketPlayerConnectionProvider implements ConnectPlayerPort {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "8765";

    private final BackendWebSocketSession backendSession;

    public WebSocketPlayerConnectionProvider() {
        this(new BackendWebSocketSession(resolveEndpointUri(), DEFAULT_TIMEOUT, new JavaNetWebSocketSessionClient()));
    }

    public WebSocketPlayerConnectionProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public void connectPlayer(String playerName) {
        backendSession.connect(playerName);
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

