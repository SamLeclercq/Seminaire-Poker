package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.CreateTablePort;

import java.util.Objects;

public class CreateTableService {
    private final CreateTablePort createTablePort;
    private final TableCodeValidator tableCodeValidator;

    public CreateTableService(CreateTablePort createTablePort, TableCodeValidator tableCodeValidator) {
        this.createTablePort = Objects.requireNonNull(createTablePort, "createTablePort must not be null");
        this.tableCodeValidator = Objects.requireNonNull(tableCodeValidator, "tableCodeValidator must not be null");
    }

    public String createTable() {
        String tableCode = tableCodeValidator.normalize(createTablePort.createTable());
        if (!tableCodeValidator.isValid(tableCode)) {
            throw new IllegalStateException("Created table code must contain exactly five characters");
        }
        return tableCode;
    }
}

