package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.JoinTablePort;

import java.util.Locale;
import java.util.Objects;

public class WebSocketJoinTableProvider implements JoinTablePort {
    private final BackendWebSocketSession backendSession;

    public WebSocketJoinTableProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public boolean joinTable(String tableCode) {
        String normalizedCode = tableCode == null ? "" : tableCode.trim().toUpperCase(Locale.ROOT);
        String request = "{\"action\":\"join\",\"payload\":{\"tableId\":\"" + normalizedCode + "\"}}";

        BackendWebSocketSession.BackendEnvelope response = backendSession.sendAction(
                request,
                "Unable to join table through backend websocket"
        );

        return response != null && "success".equals(response.status);
    }
}

