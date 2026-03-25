package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.DisconnectPlayerPort;

import java.util.Objects;

public class DisconnectPlayerService {
    private final DisconnectPlayerPort disconnectPlayerPort;

    public DisconnectPlayerService(DisconnectPlayerPort disconnectPlayerPort) {
        this.disconnectPlayerPort = Objects.requireNonNull(disconnectPlayerPort, "disconnectPlayerPort must not be null");
    }

    public void disconnectPlayer() {
        disconnectPlayerPort.disconnectPlayer();
    }
}

