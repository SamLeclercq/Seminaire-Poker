package com.seminairepoker.frontend.infrastructure.provider;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record BackendTableStateTransport(
        String status,
        String action,
        String message,
        BackendTableStatePayloadTransport data,
        @JsonAlias({"tableId", "table_id"}) String tableId,
        String currentState,
        Integer pot,
        List<Object> communityCards,
        List<Object> playerPocket,
        List<BackendPlayerTransport> players
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record BackendTableStatePayloadTransport(
        @JsonAlias({"tableId", "table_id"}) String tableId,
        String currentState,
        Integer pot,
        List<Object> communityCards,
        List<Object> playerPocket,
        List<BackendPlayerTransport> players
) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record BackendPlayerTransport(
        String playerName,
        Integer balance,
        Boolean isDealer,
        Boolean isInTurn
) {
}

