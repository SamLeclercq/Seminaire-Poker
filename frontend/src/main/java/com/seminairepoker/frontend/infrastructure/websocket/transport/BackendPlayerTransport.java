package com.seminairepoker.frontend.infrastructure.websocket.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BackendPlayerTransport(
        String playerName,
        Integer balance,
        Boolean isDealer,
        Boolean isInTurn
) {
}

