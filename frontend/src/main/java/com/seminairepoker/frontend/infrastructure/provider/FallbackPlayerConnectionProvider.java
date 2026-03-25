package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;
import com.seminairepoker.frontend.application.port.DisconnectPlayerPort;

import java.util.Objects;

public class FallbackPlayerConnectionProvider implements ConnectPlayerPort, DisconnectPlayerPort {
    private final ConnectPlayerPort primaryConnectProvider;
    private final ConnectPlayerPort fallbackConnectProvider;
    private final DisconnectPlayerPort primaryDisconnectProvider;
    private final DisconnectPlayerPort fallbackDisconnectProvider;

    public FallbackPlayerConnectionProvider(
            ConnectPlayerPort primaryConnectProvider,
            DisconnectPlayerPort primaryDisconnectProvider,
            ConnectPlayerPort fallbackConnectProvider,
            DisconnectPlayerPort fallbackDisconnectProvider
    ) {
        this.primaryConnectProvider = Objects.requireNonNull(primaryConnectProvider, "primaryConnectProvider must not be null");
        this.primaryDisconnectProvider = Objects.requireNonNull(primaryDisconnectProvider, "primaryDisconnectProvider must not be null");
        this.fallbackConnectProvider = Objects.requireNonNull(fallbackConnectProvider, "fallbackConnectProvider must not be null");
        this.fallbackDisconnectProvider = Objects.requireNonNull(fallbackDisconnectProvider, "fallbackDisconnectProvider must not be null");
    }

    @Override
    public void connectPlayer(String playerName) {
        try {
            primaryConnectProvider.connectPlayer(playerName);
        } catch (RuntimeException primaryFailure) {
            fallbackConnectProvider.connectPlayer(playerName);
        }
    }

    @Override
    public void disconnectPlayer() {
        try {
            primaryDisconnectProvider.disconnectPlayer();
        } catch (RuntimeException primaryFailure) {
            fallbackDisconnectProvider.disconnectPlayer();
        }
    }
}

