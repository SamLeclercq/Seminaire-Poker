package com.seminairepoker.frontend.infrastructure.websocket.transport;

public record BackendActionRequestTransport(String action, Object payload) {
    public static BackendActionRequestTransport create() {
        return new BackendActionRequestTransport("create", new BackendCreatePayloadTransport());
    }

    public static BackendActionRequestTransport join(String tableId) {
        return new BackendActionRequestTransport("join", new BackendJoinPayloadTransport(tableId));
    }

    public static BackendActionRequestTransport ready(String tableId) {
        return new BackendActionRequestTransport("ready", new BackendJoinPayloadTransport(tableId));
    }
}

