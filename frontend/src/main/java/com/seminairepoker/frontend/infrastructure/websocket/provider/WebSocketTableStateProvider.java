package com.seminairepoker.frontend.infrastructure.websocket.provider;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.infrastructure.websocket.adapter.BackendTableStateAdapter;
import com.seminairepoker.frontend.infrastructure.websocket.session.BackendWebSocketSession;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;

import java.util.Objects;

public class WebSocketTableStateProvider implements TableStateProvider {
    private final BackendWebSocketSession backendSession;
    private final BackendTableStateAdapter backendTableStateAdapter;

    public WebSocketTableStateProvider(BackendWebSocketSession backendSession) {
        this.backendSession = Objects.requireNonNull(backendSession, "backendSession must not be null");
        this.backendTableStateAdapter = new BackendTableStateAdapter();
    }

    @Override
    public TableState loadInitialState() {
        BackendTableStatePayloadTransport statePayload = backendSession.requireLastKnownState();
        return backendTableStateAdapter.toTableState(statePayload);
    }

}

