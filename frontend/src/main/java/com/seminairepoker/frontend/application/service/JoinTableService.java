package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.JoinTablePort;

import java.util.Objects;

public class JoinTableService {
    private final JoinTablePort joinTablePort;
    private final TableCodeValidator tableCodeValidator;

    public JoinTableService(JoinTablePort joinTablePort, TableCodeValidator tableCodeValidator) {
        this.joinTablePort = Objects.requireNonNull(joinTablePort, "joinTablePort must not be null");
        this.tableCodeValidator = Objects.requireNonNull(tableCodeValidator, "tableCodeValidator must not be null");
    }

    public boolean joinTable(String tableCode) {
        String normalized = tableCodeValidator.normalize(tableCode);
        if (!tableCodeValidator.isValid(normalized)) {
            return false;
        }
        return joinTablePort.joinTable(normalized);
    }
}

