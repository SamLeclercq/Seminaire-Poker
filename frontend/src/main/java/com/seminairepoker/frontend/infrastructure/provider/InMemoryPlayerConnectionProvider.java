package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;

public class InMemoryPlayerConnectionProvider implements ConnectPlayerPort {
    private String connectedPlayerName;

    @Override
    public void connectPlayer(String playerName) {
        connectedPlayerName = playerName;
    }


    public boolean isConnected() {
        return connectedPlayerName != null;
    }
}

