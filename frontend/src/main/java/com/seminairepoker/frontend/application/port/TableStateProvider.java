package com.seminairepoker.frontend.application.port;

import com.seminairepoker.frontend.presentation.state.TableUiState;

public interface TableStateProvider {
    TableUiState loadInitialState();
}

