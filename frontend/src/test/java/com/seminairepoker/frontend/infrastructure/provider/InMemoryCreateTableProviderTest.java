package com.seminairepoker.frontend.infrastructure.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryCreateTableProviderTest {

    @Test
    void should_return_five_characters_when_create_table_is_requested() {
        // Arrange
        InMemoryCreateTableProvider provider = new InMemoryCreateTableProvider(() -> "AB123", code -> { });

        // Act
        String tableCode = provider.createTable();

        // Assert
        assertEquals(5, tableCode.length());
        assertTrue(tableCode.matches("[A-Z0-9]{5}"));
    }
}

