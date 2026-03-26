package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class BackendTableStateAdapter {

    TableState toTableState(BackendTableStatePayloadTransport payload) {
        List<String> communityCards = stringifyCards(payload.communityCards());
        List<String> localPlayerCards = stringifyCards(payload.playerPocket());
        List<PlayerSeatState> seats = mapSeats(payload.players());

        return new TableState(
                resolveTableCode(payload.tableId()),
                resolveRoundLabel(payload.currentState()),
                payload.pot() == null ? 0 : payload.pot(),
                communityCards,
                localPlayerCards,
                seats
        );
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

            seats.add(new PlayerSeatState(index + 1, playerName, balance, isDealer, true, isInTurn));
        }
        return List.copyOf(seats);
    }

    private String resolveTableCode(String tableCode) {
        if (tableCode == null || tableCode.isBlank()) {
            return "LOCAL";
        }
        return tableCode.trim().toUpperCase(Locale.ROOT);
    }

    private String resolveRoundLabel(String currentState) {
        if (currentState == null || currentState.isBlank()) {
            return "Waiting";
        }
        return currentState;
    }
}

