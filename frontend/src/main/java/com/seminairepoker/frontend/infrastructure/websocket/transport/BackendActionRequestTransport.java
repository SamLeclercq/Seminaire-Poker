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

    public static BackendActionRequestTransport check(String tableId) {
        return new BackendActionRequestTransport("check", new BackendJoinPayloadTransport(tableId));
    }

    public static BackendActionRequestTransport call(String tableId) {
        return new BackendActionRequestTransport("call", new BackendJoinPayloadTransport(tableId));
    }

    public static BackendActionRequestTransport fold(String tableId) {
        return new BackendActionRequestTransport("fold", new BackendJoinPayloadTransport(tableId));
    }

    public static BackendActionRequestTransport bet(String tableId, int amount) {
        return new BackendActionRequestTransport("bet", new BackendAmountActionPayloadTransport(tableId, amount));
    }

    public static BackendActionRequestTransport raise(String tableId, int amount) {
        return new BackendActionRequestTransport("raise", new BackendAmountActionPayloadTransport(tableId, amount));
    }

    public static BackendActionRequestTransport customTableAction(String action, String tableId) {
        return new BackendActionRequestTransport(action, new BackendJoinPayloadTransport(tableId));
    }
}

