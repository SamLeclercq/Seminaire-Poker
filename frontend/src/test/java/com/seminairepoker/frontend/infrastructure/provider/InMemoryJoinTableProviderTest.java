package com.seminairepoker.frontend.infrastructure.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryJoinTableProviderTest {

    @Test
    void shouldReturnFalseByDefault_whenNoPolicyIsProvided() {
        // Arrange
        InMemoryJoinTableProvider provider = new InMemoryJoinTableProvider();

        // Act
        boolean canJoin = provider.joinTable("AB123");

        // Assert
        assertFalse(canJoin);
    }

    @Test
    void shouldReturnTrue_whenTableExists() {
        // Arrange
        InMemoryJoinTableProvider provider = new InMemoryJoinTableProvider(code -> "AB123".equals(code));

        // Act
        boolean canJoin = provider.joinTable("ab123");

        // Assert
        assertTrue(canJoin);
    }

    @Test
    void shouldReturnFalse_whenTableDoesNotExist() {
        // Arrange
        InMemoryJoinTableProvider provider = new InMemoryJoinTableProvider(code -> false);

        // Act
        boolean canJoin = provider.joinTable("AB123");

        // Assert
        assertFalse(canJoin);
    }
}

