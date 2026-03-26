package com.seminairepoker.frontend.application.model;

import java.util.List;

public record TableState(
        String tableCode,
        String roundLabel,
        int pot,
        List<String> communityCards,
        List<String> localPlayerCards,
        List<PlayerSeatState> seats,
        List<String> legalActions,
        int currentBet,
        int localPlayerStack
) {
    public TableState(
            String tableCode,
            String roundLabel,
            int pot,
            List<String> communityCards,
            List<String> localPlayerCards,
            List<PlayerSeatState> seats
    ) {
        this(tableCode, roundLabel, pot, communityCards, localPlayerCards, seats, List.of(), 0, 0);
    }

    public TableState {
        legalActions = legalActions == null ? List.of() : List.copyOf(legalActions);
    }
}

