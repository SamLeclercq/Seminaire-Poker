package com.seminairepoker.frontend.application.port;

import com.seminairepoker.frontend.application.model.TableState;

import java.util.function.Consumer;

public interface TableStateProvider {
    TableState loadInitialState();

    Runnable subscribe(Consumer<TableState> onTableStateUpdated);
}

