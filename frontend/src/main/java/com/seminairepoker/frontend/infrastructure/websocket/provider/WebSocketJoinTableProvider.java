package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.port.JoinTablePort;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendActionRequestTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStateTransport;

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

        BackendTableStateTransport response = backendSession.sendAction(
                BackendActionRequestTransport.join(normalizedCode),
                "Unable to join table through backend websocket"
        );

        return response != null && response.isActionSuccess("join");
    }
}


