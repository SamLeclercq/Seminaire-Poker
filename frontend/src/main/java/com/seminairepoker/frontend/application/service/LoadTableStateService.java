package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.TableStateProvider;

import java.util.Objects;

public class LoadTableStateService {
    private final TableStateProvider tableStateProvider;

    public LoadTableStateService(TableStateProvider tableStateProvider) {
        this.tableStateProvider = Objects.requireNonNull(tableStateProvider, "tableStateProvider must not be null");
    }

    public TableState loadInitialState() {
        return tableStateProvider.loadInitialState();
    }
}

