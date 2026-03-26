package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;
import com.seminairepoker.frontend.infrastructure.websocket.client.JavaNetWebSocketSessionClient;
import com.seminairepoker.frontend.infrastructure.websocket.config.WsEndpointResolver;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;

import java.time.Duration;
import java.util.Objects;

public class WebSocketPlayerConnectionProvider implements ConnectPlayerPort {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);

    private final BackendWebSocketSession backendSession;

    public WebSocketPlayerConnectionProvider() {
        this(new BackendWebSocketSession(WsEndpointResolver.resolve(), DEFAULT_TIMEOUT, new JavaNetWebSocketSessionClient()));
    }

    public WebSocketPlayerConnectionProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public void connectPlayer(String playerName) {
        backendSession.connect(playerName);
    }
}


