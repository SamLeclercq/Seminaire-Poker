package com.seminairepoker.frontend.presentation.state;

import java.util.List;
import java.util.Locale;

public record TableUiState(
        String tableCode,
        String roundLabel,
        int pot,
        List<String> communityCards,
        List<String> localPlayerCards,
        List<PlayerSeatUiState> seats,
        boolean waitingForReady,
        boolean localPlayerReady,
        List<String> legalActions,
        int currentBet,
        int localPlayerStack
) {
    public TableUiState(
            String tableCode,
            String roundLabel,
            int pot,
            List<String> communityCards,
            List<String> localPlayerCards,
            List<PlayerSeatUiState> seats,
            boolean waitingForReady,
            boolean localPlayerReady
    ) {
        this(
                tableCode,
                roundLabel,
                pot,
                communityCards,
                localPlayerCards,
                seats,
                waitingForReady,
                localPlayerReady,
                List.of(),
                0,
                0
        );
    }

    public TableUiState(
            String tableCode,
            String roundLabel,
            int pot,
            List<String> communityCards,
            List<String> localPlayerCards,
            List<PlayerSeatUiState> seats
    ) {
        this(
                tableCode,
                roundLabel,
                pot,
                communityCards,
                localPlayerCards,
                seats,
                isWaitingRound(roundLabel),
                resolveLocalPlayerReady(seats),
                List.of(),
                0,
                0
        );
    }

    public TableUiState withTableCode(String newTableCode) {
        return new TableUiState(
                newTableCode,
                roundLabel,
                pot,
                communityCards,
                localPlayerCards,
                seats,
                waitingForReady,
                localPlayerReady,
                legalActions,
                currentBet,
                localPlayerStack
        );
    }

    public TableUiState {
        legalActions = legalActions == null ? List.of() : List.copyOf(legalActions);
    }

    private static boolean isWaitingRound(String roundLabel) {
        if (roundLabel == null) {
            return false;
        }
        return "waiting".equals(roundLabel.trim().toLowerCase(Locale.ROOT));
    }

    private static boolean resolveLocalPlayerReady(List<PlayerSeatUiState> seats) {
        if (seats == null) {
            return false;
        }
        return seats.stream()
                .filter(PlayerSeatUiState::currentPlayer)
                .findFirst()
                .map(PlayerSeatUiState::ready)
                .orElse(false);
    }
}
