package com.seminairepoker.frontend.application.model;

public record PlayerSeatState(
        int seatIndex,
        String playerName,
        int stack,
        boolean dealer,
        boolean occupied,
        boolean acting,
        boolean currentPlayer,
        boolean ready
) {
    public PlayerSeatState(int seatIndex, String playerName, int stack, boolean dealer, boolean occupied, boolean acting) {
        this(seatIndex, playerName, stack, dealer, occupied, acting, false, false);
    }
}

