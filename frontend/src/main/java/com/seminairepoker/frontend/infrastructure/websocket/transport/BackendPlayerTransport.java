package com.seminairepoker.frontend.infrastructure.websocket.transport;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
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
        @JsonAlias({"isFolded", "is_folded"}) Boolean isFolded,
        @JsonAlias({"isAllIn", "is_all_in"}) Boolean isAllIn,
        @JsonAlias({"currentBet", "current_bet"}) Integer currentBet,
        @JsonAlias({"lastAction", "last_action"}) String lastAction,
        Integer wins,
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
            Boolean isFolded,
            Boolean isAllIn,
            List<?> pocket
    ) {
        this(playerName, balance, isDealer, isInTurn, isCurrentPlayer, isReady, isConnected, isActive, isFolded, isAllIn, 0, null, 0, normalizePocket(pocket));
    }

    public BackendPlayerTransport(
            String playerName,
            Integer balance,
            Boolean isDealer,
            Boolean isInTurn,
            Boolean isCurrentPlayer,
            Boolean isReady,
            Boolean isConnected,
            Boolean isActive,
            List<?> pocket
    ) {
        this(playerName, balance, isDealer, isInTurn, isCurrentPlayer, isReady, isConnected, isActive, false, false, 0, null, 0, normalizePocket(pocket));
    }

    public BackendPlayerTransport(
            String playerName,
            Integer balance,
            Boolean isDealer,
            Boolean isInTurn,
            Boolean isCurrentPlayer,
            Boolean isReady,
            Boolean isConnected,
            Boolean isActive,
            Integer currentBet,
            List<?> pocket
    ) {
        this(
                playerName,
                balance,
                isDealer,
                isInTurn,
                isCurrentPlayer,
                isReady,
                isConnected,
                isActive,
                false,
                false,
                currentBet == null ? 0 : currentBet,
                null,
                0,
                normalizePocket(pocket)
        );
    }

    private static List<Object> normalizePocket(List<?> pocket) {
        if (pocket == null || pocket.isEmpty()) {
            return List.of();
        }
        List<Object> normalized = new ArrayList<>(pocket.size());
        normalized.addAll(pocket);
        return List.copyOf(normalized);
    }
}

