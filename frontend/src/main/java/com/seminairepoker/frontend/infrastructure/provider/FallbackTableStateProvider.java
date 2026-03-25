package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.presentation.state.TableUiState;

import java.util.Objects;

public class FallbackTableStateProvider implements TableStateProvider {
    private final TableStateProvider primaryProvider;
    private final TableStateProvider fallbackProvider;

    public FallbackTableStateProvider(TableStateProvider primaryProvider, TableStateProvider fallbackProvider) {
        this.primaryProvider = Objects.requireNonNull(primaryProvider, "primaryProvider must not be null");
        this.fallbackProvider = Objects.requireNonNull(fallbackProvider, "fallbackProvider must not be null");
    }

    @Override
    public TableUiState loadInitialState() {
        try {
            return primaryProvider.loadInitialState();
        } catch (RuntimeException primaryFailure) {
            return fallbackProvider.loadInitialState();
        }
    }
}

