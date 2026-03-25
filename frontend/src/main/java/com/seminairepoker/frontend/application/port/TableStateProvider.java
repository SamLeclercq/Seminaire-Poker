package com.seminairepoker.frontend.application.port;

import com.seminairepoker.frontend.application.model.TableState;

public interface TableStateProvider {
    TableState loadInitialState();
}

