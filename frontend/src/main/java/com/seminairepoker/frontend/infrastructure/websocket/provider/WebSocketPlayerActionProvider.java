package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.port.PlayerActionPort;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendActionRequestTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStateTransport;

import java.util.Objects;

public class WebSocketPlayerActionProvider implements PlayerActionPort {
    private final BackendWebSocketSession backendSession;

    public WebSocketPlayerActionProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
    }

    @Override
    public boolean check(String tableCode) {
        return sendAction(BackendActionRequestTransport.check(tableCode), "Unable to check through backend websocket");
    }

    @Override
    public boolean call(String tableCode) {
        return sendAction(BackendActionRequestTransport.call(tableCode), "Unable to call through backend websocket");
    }

    @Override
    public boolean fold(String tableCode) {
        return sendAction(BackendActionRequestTransport.fold(tableCode), "Unable to fold through backend websocket");
    }

    @Override
    public boolean bet(String tableCode, int amount) {
        return sendAction(BackendActionRequestTransport.bet(tableCode, amount), "Unable to bet through backend websocket");
    }

    @Override
    public boolean raise(String tableCode, int amount) {
        return sendAction(BackendActionRequestTransport.raise(tableCode, amount), "Unable to raise through backend websocket");
    }

    private boolean sendAction(BackendActionRequestTransport request, String failureMessage) {
        BackendTableStateTransport response = backendSession.sendAction(request, failureMessage);
        return response != null && response.isSuccessStatus();
    }
}

