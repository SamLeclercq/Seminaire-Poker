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
        boolean localPlayerReady
) {
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
                resolveLocalPlayerReady(seats)
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
                localPlayerReady
        );
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
