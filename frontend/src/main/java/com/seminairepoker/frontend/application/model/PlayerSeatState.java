package com.seminairepoker.frontend.application.model;

import java.util.List;

public record PlayerSeatState(
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
    public PlayerSeatState(
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

    public PlayerSeatState(int seatIndex, String playerName, int stack, boolean dealer, boolean occupied, boolean acting) {
        this(seatIndex, playerName, stack, dealer, occupied, acting, false, false, List.of());
    }

    public PlayerSeatState {
        cards = cards == null ? List.of() : List.copyOf(cards);
    }
}

