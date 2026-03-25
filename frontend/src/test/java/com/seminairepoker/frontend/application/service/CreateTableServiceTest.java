package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.CreateTablePort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateTableServiceTest {

    @Test
    void should_create_table_session_when_create_table_is_requested() {
        // Arrange
        CreateTablePort createTablePort = () -> "ab123";
        CreateTableService createTableService = new CreateTableService(createTablePort, new TableCodeValidator());

        // Act
        String tableCode = createTableService.createTable();

        // Assert
        assertEquals("AB123", tableCode);
    }

    @Test
    void should_throw_exception_when_provider_returns_invalid_table_code() {
        // Arrange
        CreateTablePort createTablePort = () -> "ABC";
        CreateTableService createTableService = new CreateTableService(createTablePort, new TableCodeValidator());

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class, createTableService::createTable);

        // Assert
        assertEquals("Created table code must contain exactly five characters", exception.getMessage());
    }
}

