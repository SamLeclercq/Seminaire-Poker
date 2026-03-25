package com.seminairepoker.frontend.presentation.state;

import java.util.List;

public record TableUiState(
        String tableCode,
        String roundLabel,
        int pot,
        List<String> communityCards,
        List<String> localPlayerCards,
        List<PlayerSeatUiState> seats
) {
    public TableUiState withTableCode(String newTableCode) {
        return new TableUiState(newTableCode, roundLabel, pot, communityCards, localPlayerCards, seats);
    }
}
