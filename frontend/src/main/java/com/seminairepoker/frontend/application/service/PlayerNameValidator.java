package com.seminairepoker.frontend.application.service;

public class PlayerNameValidator {

    public boolean isValid(String playerName) {
        return !normalize(playerName).isBlank();
    }

    public String normalize(String playerName) {
        if (playerName == null) {
            return "";
        }
        return playerName.trim();
    }
}

