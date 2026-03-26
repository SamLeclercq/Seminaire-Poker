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
        @JsonAlias({"currentBet", "current_bet"}) Integer currentBet,
        @JsonAlias({"pocket", "playerPocket", "player_pocket", "cards"}) List<Object> pocket
) {
    public BackendPlayerTransport(
            String playerName,
            Integer balance,
            Boolean isDealer,
            Boolean isInTurn,
            Boolean isCurrentPlayer,
            Boolean isReady,
            Boolean isConnected,
            Boolean isActive,
            List<Object> pocket
    ) {
        this(playerName, balance, isDealer, isInTurn, isCurrentPlayer, isReady, isConnected, isActive, 0, pocket);
    }
}

