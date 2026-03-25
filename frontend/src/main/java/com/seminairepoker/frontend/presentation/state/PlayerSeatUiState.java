package com.seminairepoker.frontend.presentation.state;

public record PlayerSeatUiState(
        int seatIndex,
        String playerName,
        int stack,
        boolean dealer,
        boolean occupied,
        boolean acting
) {
}

