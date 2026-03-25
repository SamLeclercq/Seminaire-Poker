package com.seminairepoker.frontend.infrastructure.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryJoinTableProviderTest {

    @Test
    void should_return_false_by_default_when_no_policy_is_provided() {
        // Arrange
        InMemoryJoinTableProvider provider = new InMemoryJoinTableProvider();

        // Act
        boolean canJoin = provider.joinTable("AB123");

        // Assert
        assertFalse(canJoin);
    }

    @Test
    void should_return_true_when_table_exists() {
        // Arrange
        InMemoryJoinTableProvider provider = new InMemoryJoinTableProvider(code -> "AB123".equals(code));

        // Act
        boolean canJoin = provider.joinTable("ab123");

        // Assert
        assertTrue(canJoin);
    }

    @Test
    void should_return_false_when_table_does_not_exist() {
        // Arrange
        InMemoryJoinTableProvider provider = new InMemoryJoinTableProvider(code -> false);

        // Act
        boolean canJoin = provider.joinTable("AB123");

        // Assert
        assertFalse(canJoin);
    }
}

