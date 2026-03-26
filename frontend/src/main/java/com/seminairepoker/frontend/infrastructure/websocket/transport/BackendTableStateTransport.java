package com.seminairepoker.frontend.infrastructure.websocket.transport;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BackendTableStateTransport(
        String status,
        String action,
        String message,
        BackendTableStatePayloadTransport data,
        @JsonAlias({"tableId", "table_id"}) String tableId,
        @JsonAlias({"currentState", "current_state", "currentHand", "current_hand"}) String currentState,
        Integer pot,
        List<Object> communityCards,
        List<Object> playerPocket,
        List<BackendPlayerTransport> players
) {
    public boolean isSuccessStatus() {
        return "success".equals(status);
    }

    public boolean isActionSuccess(String expectedAction) {
        return isSuccessStatus() && expectedAction != null && expectedAction.equals(action);
    }

    public boolean hasDirectStatePayload() {
        return hasDirectGameState();
    }

    public BackendTableStatePayloadTransport resolveStatePayload() {
        if (data != null) {
            return data;
        }

        if (!hasDirectGameState()) {
            return null;
        }

        return new BackendTableStatePayloadTransport(
                tableId,
                currentState,
                pot,
                communityCards,
                playerPocket,
                players
        );
    }

    private boolean hasDirectGameState() {
        return tableId != null
                || currentState != null
                || pot != null
                || communityCards != null
                || playerPocket != null
                || players != null;
    }
}

