package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;

import java.util.Objects;

public class ConnectPlayerService {
    private final ConnectPlayerPort connectPlayerPort;
    private final PlayerNameValidator playerNameValidator;

    public ConnectPlayerService(ConnectPlayerPort connectPlayerPort, PlayerNameValidator playerNameValidator) {
        this.connectPlayerPort = Objects.requireNonNull(connectPlayerPort, "connectPlayerPort must not be null");
        this.playerNameValidator = Objects.requireNonNull(playerNameValidator, "playerNameValidator must not be null");
    }

    public boolean connectPlayer(String playerName) {
        String normalizedPlayerName = playerNameValidator.normalize(playerName);
        if (!playerNameValidator.isValid(normalizedPlayerName)) {
            return false;
        }
        connectPlayerPort.connectPlayer(normalizedPlayerName);
        return true;
    }
}

