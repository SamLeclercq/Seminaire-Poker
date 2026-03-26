package com.seminairepoker.frontend.infrastructure.websocket.transport;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BackendTableStatePayloadTransport(
        @JsonAlias({"tableId", "table_id"}) String tableId,
        @JsonAlias({"currentState", "current_state"}) String currentState,
        @JsonAlias({"currentHand", "current_hand"}) Object currentHand,
        Integer pot,
        List<Object> communityCards,
        List<Object> playerPocket,
        List<BackendPlayerTransport> players
) {
}

