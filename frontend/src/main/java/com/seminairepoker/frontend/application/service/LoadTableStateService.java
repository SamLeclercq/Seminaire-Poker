package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.TableStateProvider;

import java.util.Objects;
import java.util.function.Consumer;

public class LoadTableStateService {
    private final TableStateProvider tableStateProvider;

    public LoadTableStateService(TableStateProvider tableStateProvider) {
        this.tableStateProvider = Objects.requireNonNull(tableStateProvider, "tableStateProvider must not be null");
    }

    public TableState loadInitialState() {
        return tableStateProvider.loadInitialState();
    }

    public Runnable subscribe(Consumer<TableState> onTableStateUpdated) {
        Objects.requireNonNull(onTableStateUpdated, "onTableStateUpdated must not be null");
        return tableStateProvider.subscribe(onTableStateUpdated);
    }
}

