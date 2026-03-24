package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.presentation.state.TableUiState;

import java.util.Objects;

public class LoadTableStateService {
    private final TableStateProvider tableStateProvider;

    public LoadTableStateService(TableStateProvider tableStateProvider) {
        this.tableStateProvider = Objects.requireNonNull(tableStateProvider, "tableStateProvider must not be null");
    }

    public TableUiState loadInitialState() {
        return tableStateProvider.loadInitialState();
    }
}

