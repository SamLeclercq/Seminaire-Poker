package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.ConnectPlayerPort;

import java.util.Objects;

public class FallbackPlayerConnectionProvider implements ConnectPlayerPort {
    private final ConnectPlayerPort primaryConnectProvider;
    private final ConnectPlayerPort fallbackConnectProvider;

    public FallbackPlayerConnectionProvider(
            ConnectPlayerPort primaryConnectProvider,
            ConnectPlayerPort fallbackConnectProvider
    ) {
        this.primaryConnectProvider = Objects.requireNonNull(primaryConnectProvider, "primaryConnectProvider must not be null");
        this.fallbackConnectProvider = Objects.requireNonNull(fallbackConnectProvider, "fallbackConnectProvider must not be null");
    }

    @Override
    public void connectPlayer(String playerName) {
        try {
            primaryConnectProvider.connectPlayer(playerName);
        } catch (RuntimeException primaryFailure) {
            fallbackConnectProvider.connectPlayer(playerName);
        }
    }

}

