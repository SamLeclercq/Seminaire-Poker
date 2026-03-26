package com.seminairepoker.frontend.presentation.state;

public record PlayerSeatUiState(
        int seatIndex,
        String playerName,
        int stack,
        boolean dealer,
        boolean occupied,
        boolean acting,
        boolean currentPlayer,
        boolean ready
) {
    public PlayerSeatUiState(int seatIndex, String playerName, int stack, boolean dealer, boolean occupied, boolean acting) {
        this(seatIndex, playerName, stack, dealer, occupied, acting, false, false);
    }
}

