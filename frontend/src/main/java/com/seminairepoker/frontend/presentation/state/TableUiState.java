package com.seminairepoker.frontend.presentation.state;

import java.util.List;

public record TableUiState(
        String roundLabel,
        int pot,
        List<String> communityCards,
        List<String> localPlayerCards,
        List<PlayerSeatUiState> seats
) {
}

