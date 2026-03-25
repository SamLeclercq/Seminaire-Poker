package com.seminairepoker.frontend.application.model;

import java.util.List;

public record TableState(
        String tableCode,
        String roundLabel,
        int pot,
        List<String> communityCards,
        List<String> localPlayerCards,
        List<PlayerSeatState> seats
) {
}

