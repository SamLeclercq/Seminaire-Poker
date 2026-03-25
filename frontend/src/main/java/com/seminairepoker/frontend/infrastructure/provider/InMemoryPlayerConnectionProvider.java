package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;
import com.seminairepoker.frontend.application.port.DisconnectPlayerPort;

public class InMemoryPlayerConnectionProvider implements ConnectPlayerPort, DisconnectPlayerPort {
    private String connectedPlayerName;

    @Override
    public void connectPlayer(String playerName) {
        connectedPlayerName = playerName;
    }

    @Override
    public void disconnectPlayer() {
        connectedPlayerName = null;
    }

    public boolean isConnected() {
        return connectedPlayerName != null;
    }
}

