package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.LoadTableStatePort;

import java.util.Objects;
import java.util.function.Consumer;

public class LoadTableStateService {
    private final LoadTableStatePort loadTableStatePort;

    public LoadTableStateService(LoadTableStatePort loadTableStatePort) {
        this.loadTableStatePort = Objects.requireNonNull(loadTableStatePort, "loadTableStatePort must not be null");
    }

    public TableState loadInitialState() {
        return loadTableStatePort.loadInitialState();
    }

    public Runnable subscribe(Consumer<TableState> onTableStateUpdated) {
        Objects.requireNonNull(onTableStateUpdated, "onTableStateUpdated must not be null");
        return loadTableStatePort.subscribe(onTableStateUpdated);
    }
}

