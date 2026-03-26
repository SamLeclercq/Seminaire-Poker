package com.seminairepoker.frontend.infrastructure.websocket.config;

import java.net.URI;

public final class WsEndpointResolver {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "8765";

    private WsEndpointResolver() {
    }

    public static URI resolve() {
        String wsUrl = System.getenv("POKER_WS_URL");
        if (wsUrl != null && !wsUrl.isBlank()) {
            return URI.create(wsUrl);
        }

        String host = System.getenv().getOrDefault("POKER_WS_HOST", DEFAULT_HOST);
        String port = System.getenv().getOrDefault("POKER_WS_PORT", DEFAULT_PORT);
        return URI.create("ws://" + host + ":" + port);
    }
}

