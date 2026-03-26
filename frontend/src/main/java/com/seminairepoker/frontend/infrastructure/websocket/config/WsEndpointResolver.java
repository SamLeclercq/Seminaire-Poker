package com.seminairepoker.frontend.infrastructure.websocket.config;

import java.net.URI;
import java.util.Map;

public final class WsEndpointResolver {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "8765";

    private WsEndpointResolver() {
    }

    public static URI resolve() {
        return resolve(System.getenv());
    }

    static URI resolve(Map<String, String> environment) {
        String wsUrl = environment.get("POKER_WS_URL");
        if (wsUrl != null && !wsUrl.isBlank()) {
            return URI.create(wsUrl);
        }

        String host = environment.getOrDefault("POKER_WS_HOST", DEFAULT_HOST);
        String port = environment.getOrDefault("POKER_WS_PORT", DEFAULT_PORT);
        return URI.create("ws://" + host + ":" + port);
    }
}

