package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.CreateTablePort;

import java.util.Objects;

public class WebSocketCreateTableProvider implements CreateTablePort {
    private static final String CREATE_REQUEST = "{\"action\":\"create\",\"payload\":{}}";

    private final BackendWebSocketSession backendSession;

    public WebSocketCreateTableProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public String createTable() {
        BackendWebSocketSession.BackendEnvelope response = backendSession.sendAction(
                CREATE_REQUEST,
                "Unable to create table through backend websocket"
        );

        if (response == null || !"success".equals(response.status) || response.data == null || response.data.tableId() == null) {
            throw new IllegalStateException("Backend create response does not contain a tableId");
        }

        return response.data.tableId();
    }
}

