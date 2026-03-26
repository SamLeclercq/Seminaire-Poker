package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.port.CreateTablePort;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendActionRequestTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStateTransport;

import java.util.Objects;

public class WebSocketCreateTableProvider implements CreateTablePort {
    private final BackendWebSocketSession backendSession;

    public WebSocketCreateTableProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public String createTable() {
        BackendTableStateTransport response = backendSession.sendAction(
                BackendActionRequestTransport.create(),
                "Unable to create table through backend websocket"
        );

        BackendTableStatePayloadTransport statePayload = response == null ? null : response.resolveStatePayload();

        if (response == null || !response.isActionSuccess("create") || statePayload == null || statePayload.tableId() == null) {
            throw new IllegalStateException("Backend create response does not contain a tableId");
        }

        return statePayload.tableId();
    }
}


