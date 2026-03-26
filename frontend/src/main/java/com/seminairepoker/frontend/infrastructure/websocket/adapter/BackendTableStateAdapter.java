package com.seminairepoker.frontend.infrastructure.websocket.adapter;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendPlayerTransport;
import com.seminairepoker.frontend.infrastructure.websocket.transport.BackendTableStatePayloadTransport;
import com.seminairepoker.frontend.shared.cards.CardCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BackendTableStateAdapter {
    private final BackendCardCodeNormalizer cardCodeNormalizer;

    public BackendTableStateAdapter() {
        this.cardCodeNormalizer = new BackendCardCodeNormalizer();
    }

    public TableState toTableState(BackendTableStatePayloadTransport payload) {
        List<String> communityCards = stringifyCards(payload.communityCards());
        List<String> localPlayerCards = resolveLocalPlayerCards(payload);
        List<PlayerSeatState> seats = mapSeats(payload.players());
        List<String> legalActions = normalizeLegalActions(payload.legalActions());
        int currentBet = resolveCurrentBet(payload.players());
        int localPlayerStack = resolveLocalPlayerStack(payload.players());

        return new TableState(
                resolveTableCode(payload.tableId()),
                resolveRoundLabel(payload.currentState(), payload.currentHand()),
                payload.pot() == null ? 0 : payload.pot(),
                communityCards,
                localPlayerCards,
                seats,
                legalActions,
                currentBet,
                localPlayerStack
        );
    }

    private List<String> normalizeLegalActions(List<String> legalActions) {
        if (legalActions == null || legalActions.isEmpty()) {
            return List.of();
        }

        List<String> normalized = new ArrayList<>(legalActions.size());
        for (String action : legalActions) {
            if (action == null || action.isBlank()) {
                continue;
            }
            normalized.add(action.trim().toLowerCase(Locale.ROOT));
        }
        return List.copyOf(normalized);
    }

    private int resolveCurrentBet(List<BackendPlayerTransport> players) {
        if (players == null || players.isEmpty()) {
            return 0;
        }
        int currentBet = 0;
        for (BackendPlayerTransport player : players) {
            if (player == null || player.currentBet() == null) {
                continue;
            }
            currentBet = Math.max(currentBet, player.currentBet());
        }
        return currentBet;
    }

    private int resolveLocalPlayerStack(List<BackendPlayerTransport> players) {
        if (players == null || players.isEmpty()) {
            return 0;
        }
        for (BackendPlayerTransport player : players) {
            if (player == null || !Boolean.TRUE.equals(player.isCurrentPlayer())) {
                continue;
            }
            return player.balance() == null ? 0 : player.balance();
        }
        return 0;
    }

    private List<String> resolveLocalPlayerCards(BackendTableStatePayloadTransport payload) {
        List<String> topLevelPocket = stringifyCards(payload.playerPocket());
        List<BackendPlayerTransport> players = payload.players();
        if (players == null || players.isEmpty()) {
            return topLevelPocket;
        }

        List<String> currentPlayerPocket = List.of();
        List<String> singleVisiblePocket = null;
        for (BackendPlayerTransport player : players) {
            if (player == null) {
                continue;
            }

            List<String> candidatePocket = stringifyCards(player.pocket());
            if (Boolean.TRUE.equals(player.isCurrentPlayer())) {
                currentPlayerPocket = candidatePocket;
            }

            if (!candidatePocket.isEmpty()) {
                if (singleVisiblePocket != null) {
                    singleVisiblePocket = List.of();
                    continue;
                }
                singleVisiblePocket = candidatePocket;
            }
        }

        if (containsRevealedCard(currentPlayerPocket)) {
            return currentPlayerPocket;
        }

        if (topLevelPocket.size() >= 2 && containsRevealedCard(topLevelPocket)) {
            return topLevelPocket;
        }

        if (!currentPlayerPocket.isEmpty()) {
            return currentPlayerPocket;
        }

        if (singleVisiblePocket != null && !singleVisiblePocket.isEmpty()) {
            return singleVisiblePocket;
        }

        return topLevelPocket;
    }

    private boolean containsRevealedCard(List<String> cards) {
        if (cards == null || cards.isEmpty()) {
            return false;
        }

        for (String card : cards) {
            if (!CardCodes.isHidden(card)) {
                return true;
            }
        }
        return false;
    }

    private List<String> stringifyCards(List<Object> cards) {
        if (cards == null || cards.isEmpty()) {
            return List.of();
        }
        List<String> values = new ArrayList<>(cards.size());
        for (Object card : cards) {
            values.add(cardCodeNormalizer.normalize(card));
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
            List<String> seatCards = resolveSeatCards(player.pocket());

            seats.add(new PlayerSeatState(index + 1, playerName, balance, isDealer, isOccupied, isInTurn, isCurrentPlayer, isReady, seatCards));
        }
        return List.copyOf(seats);
    }

    private List<String> resolveSeatCards(List<Object> pocket) {
        List<String> normalized = stringifyCards(pocket);
        if (normalized.size() >= 2) {
            return List.of(normalized.get(0), normalized.get(1));
        }
        if (normalized.size() == 1) {
            return List.of(normalized.getFirst(), CardCodes.HIDDEN);
        }
        return List.of(CardCodes.HIDDEN, CardCodes.HIDDEN);
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


