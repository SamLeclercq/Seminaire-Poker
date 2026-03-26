package com.seminairepoker.frontend.infrastructure.websocket.transport;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BackendPlayerTransport(
        String playerName,
        Integer balance,
        Boolean isDealer,
        Boolean isInTurn,
        @JsonAlias({"isCurrentPlayer", "is_current_player"}) Boolean isCurrentPlayer,
        @JsonAlias({"isReady", "is_ready"}) Boolean isReady,
        @JsonAlias({"isConnected", "is_connected"}) Boolean isConnected,
        @JsonAlias({"isActive", "is_active"}) Boolean isActive,
        @JsonAlias({"pocket", "playerPocket", "player_pocket", "cards"}) List<Object> pocket
) {
}

