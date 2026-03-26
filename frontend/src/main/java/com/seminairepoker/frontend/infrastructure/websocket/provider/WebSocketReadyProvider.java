package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.port.ReadyPort;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendActionRequestTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStateTransport;

import java.util.Locale;
import java.util.Objects;

public class WebSocketReadyProvider implements ReadyPort {
    private final BackendWebSocketSession backendSession;

    public WebSocketReadyProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public boolean markReady(String tableCode) {
        String normalizedCode = tableCode == null ? "" : tableCode.trim().toUpperCase(Locale.ROOT);

        BackendTableStateTransport response = backendSession.sendAction(
                BackendActionRequestTransport.ready(normalizedCode),
                "Unable to mark player ready through backend websocket"
        );

        return response != null && response.isActionSuccess("ready");
    }
}

