package com.seminairepoker.frontend.infrastructure.websocket.adapter;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendPlayerTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BackendTableStateAdapter {

    public TableState toTableState(BackendTableStatePayloadTransport payload) {
        List<String> communityCards = stringifyCards(payload.communityCards());
        List<String> localPlayerCards = resolveLocalPlayerCards(payload);
        List<PlayerSeatState> seats = mapSeats(payload.players());

        return new TableState(
                resolveTableCode(payload.tableId()),
                resolveRoundLabel(payload.currentState(), payload.currentHand()),
                payload.pot() == null ? 0 : payload.pot(),
                communityCards,
                localPlayerCards,
                seats
        );
    }

    private List<String> resolveLocalPlayerCards(BackendTableStatePayloadTransport payload) {
        List<String> topLevelPocket = stringifyCards(payload.playerPocket());
        if (topLevelPocket.size() >= 2) {
            return topLevelPocket;
        }

        List<BackendPlayerTransport> players = payload.players();
        if (players == null || players.isEmpty()) {
            return List.of();
        }

        List<String> singleVisiblePocket = null;
        for (BackendPlayerTransport player : players) {
            if (player == null) {
                continue;
            }

            List<String> candidatePocket = stringifyCards(player.pocket());
            if (Boolean.TRUE.equals(player.isCurrentPlayer())) {
                List<String> currentPlayerPocket = stringifyCards(player.pocket());
                if (!currentPlayerPocket.isEmpty()) {
                    return currentPlayerPocket;
                }
            }

            if (!candidatePocket.isEmpty()) {
                if (singleVisiblePocket != null) {
                    singleVisiblePocket = List.of();
                    continue;
                }
                singleVisiblePocket = candidatePocket;
            }
        }

        if (singleVisiblePocket != null && !singleVisiblePocket.isEmpty()) {
            return singleVisiblePocket;
        }

        return topLevelPocket;
    }

    private List<String> stringifyCards(List<Object> cards) {
        if (cards == null || cards.isEmpty()) {
            return List.of();
        }
        List<String> values = new ArrayList<>(cards.size());
        for (Object card : cards) {
            values.add(String.valueOf(card));
        }
        return List.copyOf(values);
    }

    private List<PlayerSeatState> mapSeats(List<BackendPlayerTransport> players) {
        if (players == null || players.isEmpty()) {
            return List.of();
        }

        List<PlayerSeatState> seats = new ArrayList<>(players.size());
        for (int index = 0; index < players.size(); index++) {
            BackendPlayerTransport player = players.get(index);
            if (player == null) {
                continue;
            }

            String playerName = player.playerName() == null ? "" : player.playerName();
            int balance = player.balance() == null ? 0 : player.balance();
            boolean isDealer = Boolean.TRUE.equals(player.isDealer());
            boolean isInTurn = Boolean.TRUE.equals(player.isInTurn());
            boolean isCurrentPlayer = Boolean.TRUE.equals(player.isCurrentPlayer());
            boolean isReady = Boolean.TRUE.equals(player.isReady());
            boolean isOccupied = true;

            seats.add(new PlayerSeatState(index + 1, playerName, balance, isDealer, isOccupied, isInTurn, isCurrentPlayer, isReady));
        }
        return List.copyOf(seats);
    }

    private String resolveTableCode(String tableCode) {
        if (tableCode == null || tableCode.isBlank()) {
            return "LOCAL";
        }
        return tableCode.trim().toUpperCase(Locale.ROOT);
    }

    private String resolveRoundLabel(String currentState, Object currentHand) {
        if (currentState != null && !currentState.isBlank()) {
            return currentState;
        }
        if (currentHand != null) {
            return String.valueOf(currentHand);
        }
        if (currentState == null || currentState.isBlank()) {
            return "Waiting";
        }
        return currentState;
    }
}


