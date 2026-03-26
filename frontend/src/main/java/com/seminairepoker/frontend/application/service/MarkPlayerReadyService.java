package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.ReadyPort;

import java.util.Objects;

public class MarkPlayerReadyService {
    private final ReadyPort readyPort;
    private final TableCodeValidator tableCodeValidator;

    public MarkPlayerReadyService(ReadyPort readyPort, TableCodeValidator tableCodeValidator) {
        this.readyPort = Objects.requireNonNull(readyPort, "readyPort must not be null");
        this.tableCodeValidator = Objects.requireNonNull(tableCodeValidator, "tableCodeValidator must not be null");
    }

    public boolean markReady(String tableCode) {
        String normalized = tableCodeValidator.normalize(tableCode);
        if (!tableCodeValidator.isValid(normalized)) {
            return false;
        }
        return readyPort.markReady(normalized);
    }
}

