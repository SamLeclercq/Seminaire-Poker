package com.seminairepoker.frontend.presentation.state;

import java.util.List;

public record PlayerSeatUiState(
        int seatIndex,
        String playerName,
        int stack,
        boolean dealer,
        boolean occupied,
        boolean acting,
        boolean currentPlayer,
        boolean ready,
        List<String> cards
) {
    public PlayerSeatUiState(
            int seatIndex,
            String playerName,
            int stack,
            boolean dealer,
            boolean occupied,
            boolean acting,
            boolean currentPlayer,
            boolean ready
    ) {
        this(seatIndex, playerName, stack, dealer, occupied, acting, currentPlayer, ready, List.of());
    }

    public PlayerSeatUiState(int seatIndex, String playerName, int stack, boolean dealer, boolean occupied, boolean acting) {
        this(seatIndex, playerName, stack, dealer, occupied, acting, false, false, List.of());
    }

    public PlayerSeatUiState {
        cards = cards == null ? List.of() : List.copyOf(cards);
    }
}

