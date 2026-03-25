package com.seminairepoker.frontend.application.model;

public record PlayerSeatState(
        int seatIndex,
        String playerName,
        int stack,
        boolean dealer,
        boolean occupied,
        boolean acting
) {
}

