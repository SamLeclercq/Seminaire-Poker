package com.seminairepoker.frontend.application.port;

import com.seminairepoker.frontend.application.model.TableState;

import java.util.function.Consumer;

public interface LoadTableStatePort {
    TableState loadInitialState();

    Runnable subscribe(Consumer<TableState> onTableStateUpdated);
}


